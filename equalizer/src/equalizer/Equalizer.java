package equalizer;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import player.Queue;

public class Equalizer {
	
	private short[] outputSignal;
	private Filter[] filters;
	private final static int COUNT_OF_BANDS= 8; //количество полос
	private final static char COUNT_OF_THREADS = 8; //количество потоков
	private final int lengthOfInputSignal; //длина входного сигнала
	ExecutorService pool; //многопоточность
	
	public Equalizer(final int lengthOfInputSignal) {
		pool = Executors.newFixedThreadPool(COUNT_OF_THREADS); //создаем потоки
		this.lengthOfInputSignal = lengthOfInputSignal;
		this.createFilters();
	}
	
	public void setInputSignal(Queue inputBuffer) {

		this.outputSignal = new short[this.lengthOfInputSignal];
		this.filters[0].settings(FilterInfo.COEFS_OF_BAND_0, FilterInfo.COUNT_OF_COEFS, inputBuffer);
		this.filters[1].settings(FilterInfo.COEFS_OF_BAND_1, FilterInfo.COUNT_OF_COEFS, inputBuffer);
		this.filters[2].settings(FilterInfo.COEFS_OF_BAND_2, FilterInfo.COUNT_OF_COEFS, inputBuffer);
		this.filters[3].settings(FilterInfo.COEFS_OF_BAND_3, FilterInfo.COUNT_OF_COEFS, inputBuffer);
		this.filters[4].settings(FilterInfo.COEFS_OF_BAND_4, FilterInfo.COUNT_OF_COEFS, inputBuffer);
		this.filters[5].settings(FilterInfo.COEFS_OF_BAND_5, FilterInfo.COUNT_OF_COEFS, inputBuffer);
		this.filters[6].settings(FilterInfo.COEFS_OF_BAND_6, FilterInfo.COUNT_OF_COEFS, inputBuffer);
		this.filters[7].settings(FilterInfo.COEFS_OF_BAND_7, FilterInfo.COUNT_OF_COEFS, inputBuffer);
	}
	
	
	private void createFilters() {
		this.filters = new  Filter [Equalizer.COUNT_OF_BANDS] ;
		this.filters[0] = new Filter(this.lengthOfInputSignal);
		this.filters[1] = new Filter(this.lengthOfInputSignal);
		this.filters[2] = new Filter(this.lengthOfInputSignal);
		this.filters[3] = new Filter(this.lengthOfInputSignal);
		this.filters[4] = new Filter(this.lengthOfInputSignal);
		this.filters[5] = new Filter(this.lengthOfInputSignal);
		this.filters[6] = new Filter(this.lengthOfInputSignal);
		this.filters[7] = new Filter(this.lengthOfInputSignal);
		
	}
	
	public Queue equalization(Queue outputBuffer) throws InterruptedException, ExecutionException {
		Future<short[]>[] fs = new Future[Equalizer.COUNT_OF_BANDS]; 
		for(int i = 0; i < Equalizer.COUNT_OF_BANDS; i++){ 
			fs[i] = pool.submit(this.filters[i]);
		}
		
			for(int i = 0; i < this.lengthOfInputSignal; i++) {
				try {
					outputBuffer.deleteFromBuffer();
					this.outputSignal[i] += fs[0].get()[i] + fs[1].get()[i] + fs[2].get()[i] + fs[3].get()[i] + fs[4].get()[i] + fs[5].get()[i] + fs[6].get()[i] + fs[7].get()[i];
					outputBuffer.addToRing(this.outputSignal[i]);
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}
		return outputBuffer;
	}
	
	public Filter getFilter(short nF) {
		return this.filters[nF];
	}
	
	public void close() {
		if(this.pool != null) {
			this.pool.shutdown();
		}
	}
}