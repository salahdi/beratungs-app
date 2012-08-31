package com.example.beratungskonfigurator;

public class NumberPic {
	
	private final int MINIMUM = 0;
	private final int MAXIMUM = 10;

	
	public int increment(int value){
		int valueInc;
		if( value < MAXIMUM ){
			value = value + 1;
			valueInc = value;
		}
		else{
			valueInc = MAXIMUM;
		}
		return valueInc;
	}

	public int decrement(int value){
		int valueDec;
		if( value > MINIMUM ){
			value = value - 1;
			valueDec = value;
		}
		else{
			valueDec = MINIMUM;
		}
		return valueDec;
	}

}
