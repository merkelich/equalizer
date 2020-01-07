package equalizer;

import java.util.concurrent.Callable;
import player.Queue;


public class Filter implements Callable<short[]>  {
	
	protected int countOfCoefs;
	protected double[] coefsFilter;
	protected Queue inputSignal;
	protected short[] outputSignal;
	protected double gain;
	double multiplication;
	int length;
	
	public Filter(final int lengthOfInputSignal){
		this.length = lengthOfInputSignal;
		gain = 1;
		this.outputSignal = new short[this.length];
	}
	
	public void settings(final double[] coefsFilter, final int countOfCoefs, Queue inputBuffer) {
		this.inputSignal = inputBuffer;
		this.coefsFilter =  coefsFilter;
		this.countOfCoefs = countOfCoefs;
	}
	
	public void setGain(float d) {
		this.gain = d;
	}
	
	public double getGain() {
		return this.gain;
	}

	@Override
	public short[] call() throws Exception {

		for (int i =  this.length-1; i >= 0; i--) {
			double accum = 0;
			for (int j = this.countOfCoefs-1; j >= 0; j--) {
				accum += coefsFilter[j] * (double)inputSignal.getElement(j + i);
			}
			this.outputSignal[ this.length -1 - i] = (short)(gain*accum);
		}
		return this.outputSignal;
	}

}
