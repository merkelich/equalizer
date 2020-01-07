package player;

public class Wave {
	private final boolean bigEndian;
	private final boolean signed;
	int bits;
	int channels;
	double sampleRate;
	
	public Wave() {
		this.bigEndian = false; //������ wav
		this.signed = true;
		this.bits = 16;
		this.channels  = 2;
		this.sampleRate= 44100.0;
	}
	
	public boolean isBigEndian() { //������� ����
		return this.bigEndian;
	}
	
	public boolean isSigned() { //��������
		return this.signed;
	}
	
	public int getBits() { //�����������
		return this.bits;
	}
	
	public int getChannels() { //������
		return this.channels;
	}
	
	public double getSampleRate() { //������� �������������
		return this.sampleRate;
	}
			
	
}
