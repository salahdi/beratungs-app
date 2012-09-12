package com.example.beratungskonfigurator.dialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.VideoView;

import com.example.beratungskonfigurator.R;
import com.example.beratungskonfigurator.server.ServerInterface;
import com.example.beratungskonfigurator.server.ServerInterfaceListener;

public class SzenarioDialog extends Dialog implements SurfaceHolder.Callback, OnPreparedListener, OnCompletionListener, OnBufferingUpdateListener,
		OnClickListener, OnSeekCompleteListener, OnVideoSizeChangedListener {

	private ProgressDialog pDialog;
	private ListView szenarioList;

	private int aktuelleListPosition;
	private int szenarioId;
	private int mKundeId;
	private int mAnwendungsfallId;
	private String mAnwendungsfallName;
	private TextView tBeschreibung;
	private TextView dialogTitel;
	private JSONArray szId = new JSONArray();
	private JSONArray la = new JSONArray();
	private ImageView imageView;
	ArrayList<Integer> selSzenarioList = new ArrayList<Integer>();

	// Video
	static Uri targetUri = null;
	MediaPlayer mediaPlayer;
	SurfaceView surfaceView;
	SurfaceHolder surfaceHolder;
	boolean pausing = false;
	static TextView mediaUri;
	ToggleButton toggleVideo;
	Button stopVideo;
	final Uri mediaSrc = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
	LinearLayout layVideo;
	String videoPath;
	int mpCount = 0;

	// Image Gallery
	private int[] myImageIds = { R.drawable.antartica1, R.drawable.antartica2, R.drawable.antartica3, R.drawable.antartica4 };

	// Constructor
	public SzenarioDialog(final Context context, int anwendungsfallId, int kundeId, String anwendungsfallName) {
		super(context);

		videoPath = "";

		pDialog = new ProgressDialog(context);
		pDialog.setMessage("Lade Daten!");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);

		mKundeId = kundeId;
		mAnwendungsfallId = anwendungsfallId;
		mAnwendungsfallName = anwendungsfallName;

		// get this window's layout parameters so we can change the position
		WindowManager.LayoutParams paramsLayout = getWindow().getAttributes();

		// change the position. 0,0 is center
		paramsLayout.x = 0;
		paramsLayout.y = 0;
		paramsLayout.height = WindowManager.LayoutParams.FILL_PARENT;
		paramsLayout.width = WindowManager.LayoutParams.FILL_PARENT;
		this.getWindow().setAttributes(paramsLayout);

		setCancelable(false);

		// no title on this dialog
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.szenario_dialog_layout);

		dialogTitel = (TextView) findViewById(R.id.dialogTitel);
		dialogTitel.setText(mAnwendungsfallName);

		tBeschreibung = (TextView) findViewById(R.id.tBeschreibung);
		tBeschreibung.setMovementMethod(new ScrollingMovementMethod());
		Button bClose = (Button) findViewById(R.id.bClose);
		bClose.setBackgroundResource(R.drawable.button_close);
		bClose.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				if(!videoPath.equals("")){
					mediaPlayer.release();
					mediaPlayer = null;
				}


				// ----------------------------------------------------------------------------------//
				// insertSzenario
				// ----------------------------------------------------------------------------------//

				try {
					if (selSzenarioList.isEmpty()) {
						selSzenarioList.add(0);
					}
					String selected = TextUtils.join(".", selSzenarioList);

					JSONObject updateParams = new JSONObject();
					updateParams.put("szenario", selected);
					updateParams.put("kundeId", mKundeId);

					Log.i("kundeId", "kundeId: " + mKundeId + " Szenario als String: " + selected);

					ServerInterface si = new ServerInterface();
					si.addListener(new ServerInterfaceListener() {
						public void serverSuccessHandler(JSONObject result) throws JSONException {
							Log.i("INSERT Szenario: ", result.getString("msg"));
						}

						public void serverErrorHandler(Exception e) {
							// TODO Auto-generated method
							// stub
						}
					});
					si.call("insertSzenario", updateParams);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				dismiss();

			}
		});

		// ----------------------------------------------------------------------------------//
		// gibSzenario
		// ----------------------------------------------------------------------------------//

		try {
			JSONObject params = new JSONObject();
			ServerInterface si;
			pDialog.show();

			params.put("anwendungsfallId", anwendungsfallId);

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

					la = result.getJSONArray("data");
					szId = result.getJSONArray("szenarioId");

					for (int i = 0; i < la.length(); i++) {
						HashMap<String, String> hm = new HashMap<String, String>();
						hm.put("name", la.getString(i));
						hm.put("icon", String.valueOf(R.drawable.unchecked));
						list.add(hm);
					}
					Log.i("data", la.toString());

					SimpleAdapter adapterMainList = new SimpleAdapter(context, list, R.layout.listview_kategorie, new String[] { "icon", "name" },
							new int[] { R.id.icon, R.id.name });
					szenarioList = (ListView) findViewById(R.id.szenarioList);
					szenarioList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
					szenarioList.setAdapter(adapterMainList);
					szenarioList.setItemChecked(0, true);

					// ----------------------------------------------------------------------------------//
					// gibSzenarioDetails
					// ----------------------------------------------------------------------------------//
					szenarioId = szId.getInt(0);
					Log.i("szenarioLIST:", "szenarioId: " + szenarioId);

					JSONObject params = new JSONObject();

					params.put("szenarioId", szenarioId);

					ServerInterface si = new ServerInterface();
					si.addListener(new ServerInterfaceListener() {

						public void serverSuccessHandler(JSONObject result) throws JSONException {

							tBeschreibung.setText(result.getJSONObject("data").getString("beschreibung"));
							videoPath = result.getJSONObject("data").getString("video").toString();
							Log.i("VIDEO PATH", "PATH videoPath: " + videoPath);

							if (videoPath.equals("")) {
								surfaceView.setVisibility(View.GONE);
								toggleVideo.setVisibility(View.GONE);
								stopVideo.setVisibility(View.GONE);
								mediaUri.setText("Kein Video vorhanden!");

							} else {
								setMediaPlayer();
								surfaceView.setVisibility(View.VISIBLE);
								toggleVideo.setVisibility(View.VISIBLE);
								stopVideo.setVisibility(View.VISIBLE);
								Uri playableUri = Uri.parse(videoPath);
								SzenarioDialog.setTargetUri(playableUri);
							}

							pDialog.dismiss();
						}

						public void serverErrorHandler(Exception e) {
							// z.B. Fehler Dialog aufploppen lassen
							Log.e("error", "called");
						}
					});
					si.call("gibSzenarioDetails", params);

					szenarioList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {

							mpCount = 0;
							mediaPlayer.release();
							videoPath = "";
							mediaUri.setText("");
							toggleVideo.setChecked(false);

							try {
								aktuelleListPosition = position;
								szenarioId = szId.getInt(position);
								Log.i("szenarioLIST:", "szenarioId: " + szenarioId + " Position: " + position);

								Log.i("gibSzenarioDetails", "START gibSzenarioDetails");

								// ----------------------------------------------------------------------------------//
								// gibSzenarioDetails
								// ----------------------------------------------------------------------------------//

								JSONObject params = new JSONObject();
								ServerInterface si;

								params.put("szenarioId", szenarioId);

								si = new ServerInterface();
								si.addListener(new ServerInterfaceListener() {

									public void serverSuccessHandler(JSONObject result) throws JSONException {

										tBeschreibung.setText(result.getJSONObject("data").getString("beschreibung"));
										videoPath = result.getJSONObject("data").getString("video").toString();
										Log.i("VIDEO PATH", "PATH videoPath: " + videoPath);

										if (videoPath.equals("")) {
											surfaceView.setVisibility(View.GONE);
											toggleVideo.setVisibility(View.GONE);
											stopVideo.setVisibility(View.GONE);
											mediaUri.setText("Kein Video vorhanden!");

										} else {
											setMediaPlayer();
											surfaceView.setVisibility(View.VISIBLE);
											toggleVideo.setVisibility(View.VISIBLE);
											stopVideo.setVisibility(View.VISIBLE);
											Uri playableUri = Uri.parse(videoPath);
											SzenarioDialog.setTargetUri(playableUri);
										}
									}

									public void serverErrorHandler(Exception e) {
										// z.B. Fehler Dialog aufploppen lassen
										Log.e("error", "called");
									}
								});
								si.call("gibSzenarioDetails", params);

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					});

					szenarioList.setOnItemLongClickListener(new OnItemLongClickListener() {
						public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {

							try {
								int szenarioIdLong = szId.getInt(pos);
								Log.i("Szenario ID", "SZENARIO ID: " + szenarioIdLong);
								if (selSzenarioList.contains(szenarioIdLong)) {

									for (Iterator<Integer> nameIter = selSzenarioList.iterator(); nameIter.hasNext();) {
										Integer name = nameIter.next();
										if (name == szenarioIdLong) {
											nameIter.remove();
											Log.i("REMOVE", "name REMOVE: " + name);
										}
									}
								} else {
									selSzenarioList.add(szenarioIdLong);
								}

								Log.i("LISTE", "SZENARIO ID List: " + selSzenarioList);

								List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
								for (int i = 0; i < la.length(); i++) {
									HashMap<String, String> hm = new HashMap<String, String>();
									hm.put("name", la.getString(i));
									if (selSzenarioList.contains(szId.getInt(i))) {
										hm.put("icon", String.valueOf(R.drawable.checked));
									} else {
										hm.put("icon", String.valueOf(R.drawable.unchecked));
									}
									list.add(hm);
								}
								SimpleAdapter adapterMainList = new SimpleAdapter(context, list, R.layout.listview_kategorie, new String[] { "icon",
										"name" }, new int[] { R.id.icon, R.id.name });
								szenarioList = (ListView) findViewById(R.id.szenarioList);
								szenarioList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
								szenarioList.setAdapter(adapterMainList);
								szenarioList.setItemChecked(aktuelleListPosition, true);
								Log.i("long clicked", "pos" + " " + pos);

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							return true;
						}
					});

				}

				public void serverErrorHandler(Exception e) {
					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			si.call("gibSzenario", params);

			// ----------------------------------------------------------------------------------//
			// gibKundeSzenario
			// ----------------------------------------------------------------------------------//
			Log.i("gibKundeSzenario", "START gibKundeSzenario");

			params = new JSONObject();
			pDialog.show();

			params.put("kundeId", mKundeId);

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					pDialog.dismiss();

					JSONArray selectedSId = result.getJSONArray("data");

					Log.i("JSONArray Result", "JSONArray Result: " + selectedSId);

					for (int i = 0; i < selectedSId.length(); i++) {
						selSzenarioList.add(selectedSId.getInt(i));
					}

					List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
					for (int i = 0; i < la.length(); i++) {
						HashMap<String, String> hm = new HashMap<String, String>();
						hm.put("name", la.getString(i));
						if (selSzenarioList.contains(szId.getInt(i))) {
							hm.put("icon", String.valueOf(R.drawable.checked));
						} else {
							hm.put("icon", String.valueOf(R.drawable.unchecked));
						}
						list.add(hm);
					}
					SimpleAdapter adapterMainList = new SimpleAdapter(context, list, R.layout.listview_kategorie, new String[] { "icon", "name" },
							new int[] { R.id.icon, R.id.name });
					szenarioList = (ListView) findViewById(R.id.szenarioList);
					szenarioList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
					szenarioList.setAdapter(adapterMainList);
					szenarioList.setItemChecked(0, true);
				}

				public void serverErrorHandler(Exception e) {
					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			si.call("gibKundeSzenario", params);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// get our tabHost from the xml
		TabHost tabs = (TabHost) findViewById(R.id.TabHost01);
		tabs.setup();

		// create tab 1
		TabHost.TabSpec tab1 = tabs.newTabSpec("Beschreibung");
		tab1.setContent(R.id.tBeschreibung);
		tab1.setIndicator("Beschreibung");
		tabs.addTab(tab1);

		// create tab 2
		TabHost.TabSpec tab2 = tabs.newTabSpec("Bilder");
		tab2.setContent(R.id.gBilderGallery);
		tab2.setIndicator("Bilder");
		tabs.addTab(tab2);

		// create tab 2
		final TabHost.TabSpec tab3 = tabs.newTabSpec("Video");
		tab3.setContent(R.id.vVideo);
		tab3.setIndicator("Video");
		tabs.addTab(tab3);

		tabs.setOnTabChangedListener(new OnTabChangeListener() {
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub
				Log.i("Tab Changed", "TabID: " + tabId);
				Log.i("Tab Changed", "Tab3: " + tab3);
				if (mpCount == 1) {
					mediaPlayer.pause();
					toggleVideo.setChecked(false);
				}
			}

		});

		Gallery ga = (Gallery) findViewById(R.id.gBilder);
		ga.setAdapter(new ImageAdapter(context));

		imageView = (ImageView) findViewById(R.id.gBilderImageView);

		ga.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView parent, View v, int position, long id) {
				imageView.setImageResource(myImageIds[position]);

			}

		});

	}

	static final ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSeekComplete(MediaPlayer mp) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
		// TODO Auto-generated method stub

	}

	public static void setTargetUri(Uri u) {
		targetUri = u;
		mediaUri.setText(u.toString());
		Log.i("setTargetURI", "IN method: " + targetUri);
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		// TODO Auto-generated method stub
		pDialog.dismiss();
		surfaceView.setBackgroundColor((android.R.color.transparent));
		mediaPlayer.start();
	}

	public class ImageAdapter extends BaseAdapter {

		private static final int ITEM_WIDTH = 150;
		private static final int ITEM_HEIGHT = 120;

		private Context myContext;
		private int itemBackground;

		/** Simple Constructor saving the 'parent' context. */
		public ImageAdapter(Context c) {
			this.myContext = c;
			TypedArray a = myContext.obtainStyledAttributes(R.styleable.Gallery1);
			itemBackground = a.getResourceId(R.styleable.Gallery1_android_galleryItemBackground, 0);
			a.recycle();
		}

		// inherited abstract methods - must be implemented
		// Returns count of images, and individual IDs
		public int getCount() {
			return myImageIds.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		// Returns a new ImageView to be displayed,
		public View getView(int position, View convertView, ViewGroup parent) {

			// Get a View to display image data
			ImageView iv = new ImageView(this.myContext);
			iv.setImageResource(myImageIds[position]);

			// Image should be scaled somehow
			iv.setScaleType(ImageView.ScaleType.FIT_XY);

			// Set the Width & Height of the individual images
			iv.setLayoutParams(new Gallery.LayoutParams(ITEM_WIDTH, ITEM_HEIGHT));
			iv.setBackgroundResource(itemBackground);

			return iv;
		}
	}

	public void setMediaPlayer() {

		// View view = inflater.inflate(R.layout.szenario_dialog_layout,
		// container, false);
		layVideo = (LinearLayout) findViewById(R.id.vVideo);
		mediaUri = (TextView) findViewById(R.id.mediauri);
		toggleVideo = (ToggleButton) findViewById(R.id.togglevideoplayer);
		stopVideo = (Button) findViewById(R.id.stopvideoplayer);
		surfaceView = (SurfaceView) findViewById(R.id.surfaceview);

		getWindow().setFormat(PixelFormat.UNKNOWN);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		// surfaceHolder.setFixedSize(176, 144);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		mediaPlayer = new MediaPlayer();

		mediaPlayer.setOnPreparedListener(this);
		mediaPlayer.setOnCompletionListener(this);
		mediaPlayer.setOnBufferingUpdateListener(this);
		mediaPlayer.setOnSeekCompleteListener(this);
		mediaPlayer.setScreenOnWhilePlaying(true);
		mediaPlayer.setOnVideoSizeChangedListener(this);

		toggleVideo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				if (isChecked && mpCount == 0) {
					Log.i("Toggle Beginn", "isChecked: " + isChecked + " mpCount: " + mpCount);
					mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
					mediaPlayer.setDisplay(surfaceHolder);
					mpCount = 1;
					try {
						mediaPlayer.setDataSource(getContext().getApplicationContext(), targetUri);
						pDialog.show();
						mediaPlayer.prepareAsync();
					} catch (IllegalArgumentException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (SecurityException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IllegalStateException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				} else if (!isChecked && mpCount == 1) {
					Log.i("Toggle Pause", "isChecked: " + isChecked + " mpCount: " + mpCount);
					mediaPlayer.pause();
				} else if (isChecked && mpCount == 1) {
					Log.i("Toggle Start", "isChecked: " + isChecked + " mpCount: " + mpCount);
					mediaPlayer.start();
				}
			}
		});

		stopVideo.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View v) {
				mediaPlayer.stop();
				toggleVideo.setChecked(false);
				mpCount = 0;
			}
		});

	}

}
