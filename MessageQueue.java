import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

class Message {
  private String topic;
  private String timestamp;

  public Message(String topic, String timestamp) {
    this.topic = topic;
    this.timestamp = timestamp;
  }

  public String toString() {
    return "Message{" +
            "topic='" + topic + '\'' +
            ", timestamp='" + timestamp + '\'' +
            '}';
  }
}

class MessageQueue {
  public static void main(String[] args) throws Exception {
    Queue<Message> queue = new LinkedList<>();
    Integer buffer = new Integer(10);

    Producer producerThread1 = new Producer(queue, buffer, "PRODUCER_1");
    Producer producerThread2 = new Producer(queue, buffer, "PRODUCER_2");
    Consumer consumerThread = new Consumer(queue, buffer, "CONSUMER");

    producerThread1.start();
    producerThread2.start();

    consumerThread.start();
  }
}

class Producer extends Thread {
  private Queue<Message> queue;
  private int queueSize ;

  public Producer (Queue<Message> input, int size, String threadName){
    super(threadName);
    this.queue = input;
    this.queueSize = size;
  }

  public void run() {
    int count = 1;
    while(true) {
      synchronized (queue) {
        while(queue.size() == queueSize){
          System.out.println(Thread.currentThread().getName() + " FULL : waiting...\n");
          try{
            queue.wait();
          } catch (Exception ex) {
            ex.printStackTrace();
          }
        }
        Date date = new Date();
        String topic = String.valueOf(Thread.currentThread().getId()).concat("_").concat(String.valueOf(count++));
        String currentTime = String.valueOf(date.getTime());

        Message message = new Message(topic, currentTime);
        queue.add(message);
        queue.notifyAll();
      }
    }
  }
}

class Consumer extends Thread {
  private final Queue<Message> queue;
  private int queueSize;

  public Consumer(Queue<Message> input, int size, String threadName){
    super (threadName);
    this.queue = input;
    this.queueSize = size;
  }

  public void run() {
    while (true){
      synchronized (queue) {
        while (queue.isEmpty()){
          try {
            queue.wait();
          } catch (Exception ex) {
            ex.printStackTrace();
          }
        }
        System.out.println(Thread.currentThread().getName() + " consuming... : " + queue.remove().toString());
        queue.notifyAll();
      }
    }
  }
}
