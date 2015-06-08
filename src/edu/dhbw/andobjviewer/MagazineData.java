package edu.dhbw.andobjviewer;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import edu.dhbw.andarmodelviewer.R;
import edu.dhbw.andobjviewer.sqllite.DatabaseHandler;
import edu.dhbw.andobjviewer.sqllite.MagazineContent;

public class MagazineData extends Activity{
	
	private LinearLayout mainLayout;
	private ViewFlipper viewFlipper;
    private float lastX;
    private int childCount=0;
	private SurfaceView videoView;
	private SurfaceHolder videoHolder;
	private Camera camera;
	private boolean inPreview=false;
	private boolean cameraConfigured=false;
    
    private LinearLayout.LayoutParams layoutParams;
	 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DatabaseHandler db = new DatabaseHandler(this);
		
		mainLayout = new LinearLayout(this);
		mainLayout.setOrientation(LinearLayout.VERTICAL);
		mainLayout.setBackgroundColor(Color.parseColor("#f5f5f5"));
		mainLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		
		layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(5, 5, 5, 5);
		
		viewFlipper = new ViewFlipper(this);
		viewFlipper.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		
		
		Log.d("message", "Extracting intent data");
		Intent intent = getIntent();
		String action = intent.getStringExtra("action");
		String month = intent.getStringExtra("month");
		String year = intent.getStringExtra("year");
		
		Log.d("message", "Intent data extracted....Month:"+month+" Year:"+year+" Action:"+action);
		if(action.equals("Contents")) {
			/*Log.d("Insertion", "Inserting value to database");
			Magazine magazine = new Magazine(10,2015,"May");
			db.addMagazine(magazine);
			Log.d("Completed","Insertion completed successfully");*/
			Log.d("message", "Loading contents from "+db);
			List<MagazineContent> contents = db.getContents(month, year,"content");
			Log.d("message", "Contents loaded successfully");
			setFlipView(contents);
			
		}
		else if(action.equals("Next Edition")) {
			
			childCount = 1;
			
			LinearLayout layout = new LinearLayout(this);
			layout.setOrientation(LinearLayout.VERTICAL);
			layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			layout.setGravity(Gravity.CENTER);
			
			TextView headLine = new TextView(this);
			headLine.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			headLine.setTextSize(25);
			headLine.setTextColor(Color.parseColor("#b7102f"));
			headLine.setText("Next Edition");
			
			TextView contentDesc = new TextView(this);
			contentDesc.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			contentDesc.setTextSize(15);
			contentDesc.setTextColor(Color.BLACK);
			contentDesc.setText("Next Edition of @TCS magazine will be available on 2nd July 2015");
			contentDesc.setGravity(Gravity.CENTER_VERTICAL);
			contentDesc.setGravity(Gravity.CENTER_HORIZONTAL);
			
			layout.addView(headLine,layoutParams);
			layout.addView(contentDesc,layoutParams);
			viewFlipper.addView(layout);
			
		}
		else if(action.equals("Highlights")) {
			
			List<MagazineContent> contents = db.getContents(month, year,"highlight");
			setFlipView(contents);
			
		}
		else if(action.equals("Image Preview")) {
			
			List<MagazineContent> contents = db.getContents(month, year,"cover");
			setFlipView(contents);
			
		}
		
		mainLayout.addView(viewFlipper);
		setContentView(mainLayout);
		
	}
	
	public void setFlipView(List<MagazineContent> contents) {
		
		childCount = contents.size();
		
		for(MagazineContent eachContent:contents) {
			LinearLayout layout = new LinearLayout(this);
			layout.setOrientation(LinearLayout.VERTICAL);
			layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			layout.setGravity(Gravity.CENTER);
			
			TextView headLine = new TextView(this);
			headLine.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			headLine.setTextSize(25);
			headLine.setTextColor(Color.parseColor("#b7102f"));
			headLine.setText(eachContent.getHeadLine());
			
			ImageView image = new ImageView(this);
			image.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			int resID = getResources().getIdentifier(eachContent.getImage(), "drawable",  getPackageName());
			image.setImageResource(resID);
			image.setScaleType(ScaleType.FIT_XY);
			
			TextView contentDesc = new TextView(this);
			contentDesc.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			contentDesc.setTextSize(15);
			contentDesc.setTextColor(Color.BLACK);
			contentDesc.setText(eachContent.getContent());
			contentDesc.setGravity(Gravity.CENTER_VERTICAL);
			
			layout.addView(headLine,layoutParams);
			layout.addView(image,layoutParams);
			layout.addView(contentDesc,layoutParams);
			viewFlipper.addView(layout);
		}
		
	}
	
    public boolean onTouchEvent(MotionEvent touchevent) {
    	switch (touchevent.getAction()) {
        
        case MotionEvent.ACTION_DOWN: 
        	lastX = touchevent.getX();
            break;
        case MotionEvent.ACTION_UP: 
            float currentX = touchevent.getX();
            if (lastX < currentX) {
            	
                if (viewFlipper.getDisplayedChild() == 0 || childCount==1)
                	break;
                
                viewFlipper.setInAnimation(this, R.anim.slide_in_from_left);
                viewFlipper.setOutAnimation(this, R.anim.slide_out_to_right);
                
                viewFlipper.showNext();
             }
                                     
             if (lastX > currentX) {
            	
            	 Log.d("Child",String.valueOf(viewFlipper.getDisplayedChild()));
            	 
            	 if (viewFlipper.getDisplayedChild() == 1 || childCount==1)
            		 break;
            	
            	 viewFlipper.setInAnimation(this, R.anim.slide_in_from_right);
            	 viewFlipper.setOutAnimation(this, R.anim.slide_out_to_left);
                 
                 viewFlipper.showPrevious();
             }
             break;
    	 }
         return false;
    }
    

}
