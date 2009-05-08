package skunkalone.app;

import skunkalone.android.game.GameView;
import skunkalone.android.view.MotionGestureListener;
import skunkalone.game.GameTimeReadonly;
import skunkalone.util.FPS;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class Main extends Activity {
	private final static String	TAG	= "Main";


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null)
			Log.i(TAG, "onCreate savedInstanceState is null");
		else
			Log.i(TAG, "onCreate savedInstanceState is not null");

		mGameView = new TengokuGameView(this);

		setContentView(mGameView);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		Log.i(TAG, "onRestoreInstanceState");

		mGameView.restoreState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.i(TAG, "onSaveInstanceState");

		mGameView.saveState(outState);
	}


	private GameView	mGameView;




	private static class MyMotionGestureListener extends MotionGestureListener {
		private final static String	TAG	= "MyGestureListener";

		@Override
		public void onMotionEvent(int motion, float distanceX, float distanceY) {
			switch (motion) {
			case MOTION_UP:
				Log.i(TAG, "motion up");
				return;
			case MOTION_RIGHT:
				Log.i(TAG, "motion right");
				return;
			case MOTION_DOWN:
				Log.i(TAG, "motion down");
				return;
			case MOTION_LEFT:
				Log.i(TAG, "motion left");
				return;
			}
		}

	}

	private static class TengokuGameView extends GameView {

		private final static String	TAG	= "TengokuGameView";

		public TengokuGameView(Context context) {
			super(context);

			getGameLoop().setTargetElapsedTimeByFPS(32);

			mGestureDetector = new GestureDetector(
					new MyMotionGestureListener());
		}

		@Override
		protected void onResize(int width, int height) {
			mWidth = width;
			mHeight = height;
		}

		@Override
		protected void onRestoreState(Bundle gameState) {
			Log.i(TAG, "onRestoreState");

			mCounter = gameState.getInt("Counter");
			Log.e(TAG, "Counter = " + String.valueOf(mCounter));
		}

		@Override
		protected void onSaveState(Bundle outGameState) {
			Log.i(TAG, "onSaveState");

			outGameState.putInt("Counter", mCounter);
		}

		@Override
		protected void onStart() {

			mFPS = new FPS(new skunkalone.util.IntervalTimer.TickListener() {
				@Override
				public void onTick(int updateCount) {
					mLastFps = "FPS: " + updateCount;
				}
			});
			mLastFps = "FPS: counting...";

			mCounterPaint = new Paint();
			mCounterPaint.setColor(Color.WHITE);

			super.onStart();
		}

		@Override
		protected void onStop() {
			super.onStop();

			mFPS = null;
			mLastFps = null;

			mCounterPaint = null;
		}

		@Override
		protected void onUpdate(GameTimeReadonly gameTime) {
			final long totalGameTime = gameTime.totalGameTime();

			mFPS.update(totalGameTime);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawColor(Color.BLACK);

			canvas.drawText(mLastFps, 12, 12, mCounterPaint);
			canvas.drawText(String.valueOf(mCounter), 12, 34, mCounterPaint);
			canvas.drawText(String.valueOf(getGameLoop()
					.getTargetElapsedTimeByFPS()), 12, 68, mCounterPaint);
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			final int act = event.getAction();

			if (act == MotionEvent.ACTION_DOWN) {
				return mGestureDetector.onTouchEvent(event);
			}

			if (act == MotionEvent.ACTION_UP) {
				mCounter++;

				return mGestureDetector.onTouchEvent(event);
			}

			if (act == MotionEvent.ACTION_MOVE) {
				return mGestureDetector.onTouchEvent(event);
			}

			return super.onTouchEvent(event);
		}



		private GestureDetector	mGestureDetector;

		private float			mWidth;
		private float			mHeight;

		private FPS				mFPS;
		private String			mLastFps;

		private int				mCounter;
		private Paint			mCounterPaint;

	}
}