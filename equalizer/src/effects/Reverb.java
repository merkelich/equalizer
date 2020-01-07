package effects;

import player.Queue;

public class Reverb {
	
	private Queue reverbQueue;
	private int lengthOfInput;
	private double reverbTime;
	private double reverbLength;
	private double CONST = 44.1;
	
	
	public Reverb(int length, double time) {
		this.reverbTime = time;
		this.reverbLength = this.reverbTime * this.CONST;
		this.reverbQueue = new Queue((int)reverbLength);
		reverbQueue.initQueue();
		this.lengthOfInput = length;
	}
	
	public Queue processEffect(Queue input) {
		for (int i = this.lengthOfInput - 1; i >= 0; i--) {
			this.reverbQueue.addToRing((short)(0.7 * input.getElement(i)));
			input.addToElement(i, this.reverbQueue.deleteFromBuffer1());
		}
		return input;
	}
	
	public void setTime(double time) {
		this.reverbTime = time;
	}
	
}
