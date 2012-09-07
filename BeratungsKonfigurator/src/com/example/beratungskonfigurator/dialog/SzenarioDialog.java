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
import android.content.res.TypedArray;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.beratungskonfigurator.R;
import com.example.beratungskonfigurator.server.ServerInterface;
import com.example.beratungskonfigurator.server.ServerInterfaceListener;

public class SzenarioDialog extends Dialog implements OnBufferingUpdateListener, OnCompletionListener, OnPreparedListener, OnVideoSizeChangedListener, SurfaceHolder.Callback{

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

	private static final String TAG = "MediaPlayerDemo";
    private int mVideoWidth;
    private int mVideoHeight;
    private MediaPlayer mMediaPlayer;
    private SurfaceView mPreview;
    private SurfaceHolder holder;
    private String path;
    private Bundle extras;
    private static final String MEDIA = "media";
    private static final int LOCAL_AUDIO = 1;
    private static final int STREAM_AUDIO = 2;
    private static final int RESOURCES_AUDIO = 3;
    private static final int LOCAL_VIDEO = 4;
    private static final int STREAM_VIDEO = 5;
    private boolean mIsVideoSizeKnown = false;
    private boolean mIsVideoReadyToBePlayed = false;

	private int[] myImageIds = { R.drawable.antartica1, R.drawable.antartica2, R.drawable.antartica3, R.drawable.antartica4 };

	ArrayList<Integer> selSzenarioList = new ArrayList<Integer>();

	// Constructor
	public SzenarioDialog(final Context context, int anwendungsfallId, int kundeId, String anwendungsfallName) {
		super(context);
		
		path = "rtsp://v8.cache8.c.youtube.com/CjYLENy73wIaLQlGb-3VVCUgNRMYDSANFEIJbXYtZ29vZ2xlSARSBXdhdGNoYKX6kvOXi_-kUAw=/0/0/0/video.3gp";

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
				
				releaseMediaPlayer();
		        doCleanUp();

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

					pDialog.dismiss();

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
						}

						public void serverErrorHandler(Exception e) {
							// z.B. Fehler Dialog aufploppen lassen
							Log.e("error", "called");
						}
					});
					si.call("gibSzenarioDetails", params);

					szenarioList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
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
		TabHost.TabSpec tab3 = tabs.newTabSpec("Video");
		tab3.setContent(R.id.vVideo);
		tab3.setIndicator("Video");
		tabs.addTab(tab3);

		Gallery ga = (Gallery) findViewById(R.id.gBilder);
		ga.setAdapter(new ImageAdapter(context));

		imageView = (ImageView) findViewById(R.id.gBilderImageView);

		ga.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView parent, View v, int position, long id) {
				imageView.setImageResource(myImageIds[position]);

			}

		});

		mPreview = (SurfaceView) findViewById(R.id.vVideo);
		holder = mPreview.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

	}

	static final ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

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
	
	private void playVideo(Integer Media) {
        doCleanUp();
        try {
            // Create a new media player and set the listeners
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.setDisplay(holder);
            mMediaPlayer.prepare();
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnVideoSizeChangedListener(this);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);


        } catch (Exception e) {
            Log.e(TAG, "error: " + e.getMessage(), e);
        }
    }

    public void onBufferingUpdate(MediaPlayer arg0, int percent) {
        Log.d(TAG, "onBufferingUpdate percent:" + percent);

    }

    public void onCompletion(MediaPlayer arg0) {
        Log.d(TAG, "onCompletion called");
    }

    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        Log.v(TAG, "onVideoSizeChanged called");
        if (width == 0 || height == 0) {
            Log.e(TAG, "invalid video width(" + width + ") or height(" + height + ")");
            return;
        }
        mIsVideoSizeKnown = true;
        mVideoWidth = width;
        mVideoHeight = height;
        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
            startVideoPlayback();
        }
    }

    public void onPrepared(MediaPlayer mediaplayer) {
        Log.d(TAG, "onPrepared called");
        mIsVideoReadyToBePlayed = true;
        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
            startVideoPlayback();
        }
    }

    public void surfaceChanged(SurfaceHolder surfaceholder, int i, int j, int k) {
        Log.d(TAG, "surfaceChanged called");

    }

    public void surfaceDestroyed(SurfaceHolder surfaceholder) {
        Log.d(TAG, "surfaceDestroyed called");
    }


    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated called");
        playVideo(Integer.valueOf(MEDIA));


    }

    private void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void doCleanUp() {
        mVideoWidth = 0;
        mVideoHeight = 0;
        mIsVideoReadyToBePlayed = false;
        mIsVideoSizeKnown = false;
    }

    private void startVideoPlayback() {
        Log.v(TAG, "startVideoPlayback");
        holder.setFixedSize(mVideoWidth, mVideoHeight);
        mMediaPlayer.start();
    }

}
