package com.musichub.utils;
public class ThreadController extends Thread {
	protected Object mPauseLock;
	protected boolean mPaused;
	protected boolean mFinished;
	protected boolean mStarted;

	
	public ThreadController(){
		super();
		mPauseLock = new Object();
        mPaused = false;
        mFinished = false;
        mStarted = false;
	}

	/**
	 * Call this on pause.
	 */
	public void onPause() {
		synchronized (mPauseLock) {
			mPaused = true;
		}
	}

	/**
	 * Call this on resume.
	 */
	public void onResume() {
		synchronized (mPauseLock) {
			mPaused = false;
			mPauseLock.notifyAll();
		}
	}

	@Override
	public void start(){
		super.start();
		mStarted = true;
	}
	
	public boolean isStarted(){
		return mStarted;
	}
	
	public void onStop() {
		mFinished = true;
	}
	
	public boolean isFinished(){
		return mFinished;
	}
	
	public Object getLock(){
		return mPauseLock;
	}
	
	public boolean isPaused(){
		return mPaused;
	}
}