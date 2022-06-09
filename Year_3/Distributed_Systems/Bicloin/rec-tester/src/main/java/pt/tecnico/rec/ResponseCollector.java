package pt.tecnico.rec;

import io.grpc.stub.StreamObserver;
import java.util.ArrayList;
import java.util.List;

public class ResponseCollector<R> implements StreamObserver<R> {
    volatile int responseCounter = 0;
    final int maxResponses;

    List<R> results = new ArrayList<>();
    Exception exception;
    volatile int exceptionCounter = 0;

    /** Construct thread instance with specified argument. */
    public ResponseCollector(int maxResponses) {
        this.maxResponses = maxResponses;
    }

    public synchronized List<R> getResults() {
        return this.results;
    }

    public synchronized Exception getException() {
        return this.exception;
    }

    public synchronized int getResponseCounter(){
        return this.responseCounter;
    }

    public synchronized int getExceptionCounter(){
        return this.exceptionCounter;
    }

    public synchronized void clearExceptionCounter(){
        this.exceptionCounter = 0;
    }

    @Override
    public synchronized void onNext(R r) {
        responseCounter+=1;
        results.add(r);
    }

    @Override
    public synchronized void onError(Throwable throwable) {
        this.exception = new Exception(throwable);
        this.exceptionCounter++;
    }

    @Override
    public synchronized void onCompleted() {
        if (maxResponses <= getResponseCounter()){
            this.notifyAll();
        }
    }
}
