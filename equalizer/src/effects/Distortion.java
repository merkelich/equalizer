package effects;

import player.Queue;

public class Distortion {
	
	private int lengthOfInput;
	private short AMPLITUDE = 1000;
	private short MAX_AMPLITUDE = 1000;
	private double volume = 2;
	
	public Distortion(int length) {
		this.lengthOfInput = length;
	}
	
	public Queue processEffect(Queue input) {
		for (int i =  0; i < this.lengthOfInput; i++) {
			if(Math.abs(input.getElement(i)) > AMPLITUDE) {
				input.changeElement(i, (short)((input.getElement(i)/Math.abs(input.getElement(i))) * AMPLITUDE));
			}
			input.changeElement(i, (short)(input.getElement(i) * this.volume));
		}
		return input;
	}
	
	public void setAmplitude(short value) {
		short difference;
		this.AMPLITUDE = value;
		difference  = (short)(this.MAX_AMPLITUDE - this.AMPLITUDE);
		this.volume = 2 + (double)difference * 0.0044444;
	}
	
	public double getVolume() {
		return this.volume;
	}
	
	public short getAmplitude() {
		return this.AMPLITUDE;
	}
}
