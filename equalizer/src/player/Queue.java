package player;

public class Queue {
    private short[] buffer;
    private int bufferSize; // ������������ ���������� ��������� � ������
    private int front; // ������ ������
    private int rear; // ��������� �� ��������� �������
    
    public Queue(int maxSize) {
        this.bufferSize = maxSize; //������ ������������ ���������� ��������� 
        buffer = new short[maxSize]; //������� �����
        this.rear = -1; // ��������� �������
        this.front = -1; //������ �������
    }

    public void addToRing(short a) {
	   if (!isFull()) {    
		   rear = (rear + 1) % bufferSize;
		   buffer[rear] = a;
	   }
    }
    
    public void deleteFromBuffer() {
	   if (!isEmpty()) {
		   front = (front + 1) % bufferSize;
	   }
    }
    
    public short deleteFromBuffer1() {
	   if (!isEmpty()) {
		   short a = buffer[front];
		   front = (front + 1) % bufferSize;
		   return a;
	   }
	   else {
	       return 0;
	   }
    }
    
    public void clearQueue() {
    	front = -1;
    	rear = -1;
    }
    
    public short getElement(int index) {
    	return buffer[(this.rear - index + bufferSize) % bufferSize];
    }
    
    /*public short getElement1(int index) {
    	if((index <= this.rear) && (index >= this.front)) {
    		return buffer[index];
    	}
    	else {
    		if(index > this.front) {
    			int dif = index - this.rear - 1;
    			return buffer[front + dif];
    		}
    		else {
    			int dif = this.front - index - 1;
    			return buffer[rear - dif];
    		}
    	}
    }*/
    
    public void addToElement(int index, short value) {
    	buffer[(this.rear - index + bufferSize) % bufferSize] += value;
    }
    
    
    public void changeElement(int index, short value) {
    	buffer[(this.rear - index + bufferSize) % bufferSize] = value;
    }
    
    public short[] getElements(int size) {
		if (!this.isEmpty()) {
			short[] array= new short[size];
			for (int i = 0; i < size; i++) {
				array[i] = this.buffer[(i + (bufferSize + this.rear - size + 1)) % bufferSize];
			}
			return array;
		}
		else return null;
    }
    
	public void initQueue() {
		short elem = 0;
		this.clearQueue();
		for(int i = 0; i < this.bufferSize;i++) {
			this.addToRing(elem);
		}
		front = 0;
	}
    
    public short getFront() {
    	return buffer[front]; //������ ������ ������� ������
    }

    public short getRear() {
    	return buffer[rear]; // ������ ��������� ������� ������
    }

    public boolean isFull() {
    	return (rear + 1) % bufferSize == front; //������������� ������
    }

    public boolean isEmpty() {  //�������� �� ���������� ��������� � ������
    	return front == rear;
    }
    
    
}