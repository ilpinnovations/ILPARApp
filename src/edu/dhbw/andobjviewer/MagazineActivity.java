package edu.dhbw.andobjviewer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

import com.carouseldemo.controls.Carousel;
import com.carouseldemo.controls.CarouselAdapter;
import com.carouseldemo.controls.CarouselAdapter.OnItemClickListener;
import com.carouseldemo.controls.CarouselAdapter.OnItemSelectedListener;
import com.carouseldemo.controls.CarouselItem;

import edu.dhbw.andarmodelviewer.R;
import edu.dhbw.andobjviewer.sqllite.DatabaseHandler;
import edu.dhbw.andobjviewer.sqllite.Magazine;
import edu.dhbw.andobjviewer.sqllite.MagazineContent;

public class MagazineActivity extends Activity {
	
	private SurfaceView videoView;
	private SurfaceHolder videoHolder;
	private Camera camera;
	private boolean inPreview=false;
	private boolean cameraConfigured=false;
	
	/* Text Switching attributes starts*/
	
	private RelativeLayout layout;
    
	private TextSwitcher mSwitcher;
	
	private boolean doneWelcome = false;
	
	private Handler mHandler = new Handler();
    
    String [] textToShow = new String[3];
	   
    int messageCount=textToShow.length;
    int currentIndex=-1; 
    
    Animation in,out;
	
    Runnable r=new Runnable() {
        public void run() {
            try
            {
                updateTextSwitcherText();
               
            }
            finally
            {
                mHandler.postDelayed(this, 2000);
            }
            if(doneWelcome)
            {
            	try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					mHandler.removeCallbacks(this);
					layout.setVisibility(View.GONE);
					System.out.println("Hiding the view...");
				}
            }
        }
    };
    
    private void updateTextSwitcherText()
    {
                currentIndex++;
                if(currentIndex==messageCount)
                {
                    currentIndex=0;
                    doneWelcome = true;
                }
                mSwitcher.setText(textToShow[currentIndex]);
    }
	
    /* Text Switching attributes ends*/
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Carousel carousel = (Carousel)findViewById(R.id.carousel);
        carousel.setBackgroundColor(Color.TRANSPARENT);
        
        videoView = (SurfaceView) findViewById(R.id.video_view);
		videoHolder = videoView.getHolder();
		videoHolder.addCallback(surfaceCallback);
        
        Intent intent = getIntent();
        final String month = capitalize(intent.getStringExtra("month"));
        final String year = intent.getStringExtra("year");
        textToShow = new String[]{"Welcome...","@ TCS Magazine",month+" "+year+" Edition"};
        
        /* Text Switiching starts here */
        
        layout = (RelativeLayout) findViewById(R.id.layout);
        mSwitcher = (TextSwitcher) findViewById(R.id.textSwitcher);
        
        mSwitcher.setFactory(new ViewFactory() {
            
            public View makeView() {
                // TODO Auto-generated method stub
                TextView myText = new TextView(getApplicationContext());
                myText.setGravity(Gravity.CENTER);
                myText.setTextSize(50);
                myText.setTextColor(Color.BLUE);
                myText.setAllCaps(true);
                Typeface tf = Typeface.createFromAsset(getAssets(),
                        "fonts/android_7.ttf");
                myText.setTypeface(tf,Typeface.BOLD);
                return myText;
            }
        });
        
        in = AnimationUtils.loadAnimation(this,android.R.anim.slide_in_left);
        out = AnimationUtils.loadAnimation(this,android.R.anim.slide_out_right);
        mSwitcher.setInAnimation(in);
        mSwitcher.setOutAnimation(out);

        mHandler.postDelayed(r, 1000);

        /* Text Switiching ends here */
        
        carousel.setOnItemClickListener(new OnItemClickListener(){

			public void onItemClick(CarouselAdapter<?> parent, View view,
					int position, long id) {	
				
				Intent intent = new Intent(getBaseContext(), MagazineData.class);
				intent.putExtra("action", ((CarouselItem)parent.getChildAt(position)).getName());
				intent.putExtra("month", month);
				intent.putExtra("year", year);
				startActivity(intent);
			}
        	
        });

        carousel.setOnItemSelectedListener(new OnItemSelectedListener(){

			public void onItemSelected(CarouselAdapter<?> parent, View view,
					int position, long id) {
				
				
			}

			public void onNothingSelected(CarouselAdapter<?> parent) {
			}
        	
        }
        );
        
    }
    
    private static String capitalize(String string) {
    	  char[] chars = string.toLowerCase().toCharArray();
    	  boolean found = false;
    	  for (int i = 0; i < chars.length; i++) {
    	    if (!found && Character.isLetter(chars[i])) {
    	      chars[i] = Character.toUpperCase(chars[i]);
    	      found = true;
    	    } else if (Character.isWhitespace(chars[i]) || chars[i]=='.' || chars[i]=='\'') { // You can add other chars here
    	      found = false;
    	    }
    	  }
    	  return String.valueOf(chars);
    	}
    
	  public void addData() {
		  DatabaseHandler db = new DatabaseHandler(this);
		  Magazine mag1 = new Magazine(1,2015,"May");
		  db.addMagazine(mag1);
		  Magazine mag2 = new Magazine(1,2015,"April");
		  db.addMagazine(mag2);
		  MagazineContent con1 = new MagazineContent(1,"TCS WORLD 10K BENGALURU","may2015cover","",1);
		  db.addMagazineContent(con1, "cover");
		  MagazineContent con2 = new MagazineContent(2,"TCS WORLD 10K BENGALURU","may2015content1",
				  "In the garden city of Bengaluru on May 17 for the prestigious TCS World 10K. Despite" +
				  " the heavy showers that lashed the city prior to the race, the spirit of the runners " +
				  "and the spectators could not be dampened.",1);
		  db.addMagazineContent(con2, "content");
		  MagazineContent con3 = new MagazineContent(3,"SQUARING UP","may2015content2","Let me not pray" +
		  		" to be sheltered from dangers, but to be fearless in facing them, wrote Rabindranath Tagore. " +
		  		"Charudatta Jadhav, the first Indian blind chess player to be awarded an international rating and " +
		  		"the Head of TCS Accessibility Centre of Excellence, CTO, embodies the poet’s prayer in living a " +
		  		"unique and compelling life",1);
		  db.addMagazineContent(con3, "content");
		  MagazineContent con4 = new MagazineContent(4,"A COLOURFUL JOURNEY","may2015content3","Take a drive with" +
		  		" Vasanth Rajkumar on Minnesota State Highway 61, alongside Lake Superior’s North Shore, and witness " +
		  		"nature at its most beautiful with fiery looking forests, a pebble beach, spectacular waterfalls and " +
		  		"much more",1);
		  db.addMagazineContent(con4, "content");
		  MagazineContent con5 = new MagazineContent(5,"FROM THE BIG APPLE TO THE WINDY CITY","may2015content4",
				  "The TCS Innovation Forum continues to bring exciting new developments and new age " +
				  "technologies to light with this year’s theme, Default is Digital–Prepare for the " +
				  "Next Evolution, being a key topic of conversation.",1);
		  db.addMagazineContent(con5, "content");
		  MagazineContent con6 = new MagazineContent(6,"TCS WORLD 10K BENGALURU","may2015highlight","In the garden city " +
		  		"of Bengaluru on May 17 for the prestigious TCS World 10K. Despite" +
				  " the heavy showers that lashed the city prior to the race, the spirit of the runners " +
				  "and the spectators could not be dampened.",1);
		  db.addMagazineContent(con6, "highlight");
		  MagazineContent con7 = new MagazineContent(7,"TATA INNOVISTA","april2015cover","",2);
		  db.addMagazineContent(con7, "cover");
		  MagazineContent con8 = new MagazineContent(8,"CELEBRATING A DECADE OF IDEAS","april2015content1",
				  "TCS has once again proved its value through the 10th edition of Tata Innovista by standing" +
				  " out as the company with the highest number of finalists  and winners. This undoubtedly " +
				  "demonstrates the emphasis that TCS accords to R&D within the organisation ",2);
		  db.addMagazineContent(con8, "content");
		  MagazineContent con9 = new MagazineContent(9,"TATA INNOVISTA","april2015highlight","",2);
		  db.addMagazineContent(con9, "highlight");   
		  
	  }
	  
	    @Override
		  public void onResume() {
		    super.onResume();
		    try{
		    	camera=Camera.open();
		    	startPreview();
		    } catch(Exception e) {
		    	e.printStackTrace();
		    	finish();
		    }
		  }
		    
		  @Override
		  public void onPause() {
		    if (inPreview) {
		      camera.stopPreview();
		    }
		    camera.release();
		    camera=null;
		    inPreview=false;
		          
		    super.onPause();
		  }
	    
		  private Camera.Size getBestPreviewSize(int width, int height,
	              Camera.Parameters parameters) {
			  	Camera.Size result=null;

			  	for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
			  		if (size.width<=width && size.height<=height) {
			  			if (result==null) {
			  				result=size;
			  			}
			  			else {
			  				int resultArea=result.width*result.height;
			  				int newArea=size.width*size.height;

			  				if (newArea>resultArea) {
			  					result=size;
			  				}
			  			}
			  		}
			  	}

			  	return(result);
		  }
		
		  private void initPreview(int width, int height) {
			    if (camera!=null && videoHolder.getSurface()!=null) {
			      try {
			        camera.setPreviewDisplay(videoHolder);
			      }
			      catch (Throwable t) {
			        Log.e("PreviewDemo-surfaceCallback",
			              "Exception in setPreviewDisplay()", t);
			        Toast
			          .makeText(MagazineActivity.this, t.getMessage(), Toast.LENGTH_LONG)
			          .show();
			      }

			      if (!cameraConfigured) {
			        Camera.Parameters parameters=camera.getParameters();
			        Camera.Size size=getBestPreviewSize(width, height,
			                                            parameters);
			        
			        if (size!=null) {
			          parameters.setPreviewSize(size.width, size.height);
			          camera.setParameters(parameters);
			          cameraConfigured=true;
			        }
			      }
			    }
			  }
			  
			  private void startPreview() {
			    if (cameraConfigured && camera!=null) {
			      camera.startPreview();
			      inPreview=true;
			    }
			  }
		
		 SurfaceHolder.Callback surfaceCallback=new SurfaceHolder.Callback() {
			    public void surfaceCreated(SurfaceHolder holder) {
			      // no-op -- wait until surfaceChanged()
			    }
			    
			    /*public void surfaceChanged(SurfaceHolder holder,
			                               int format, int width,
			                               int height) {
			      initPreview(width, height);
			      startPreview();
			    }*/
			    
				  
				  public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
				    {            
				        if (inPreview)
				        {
				            camera.stopPreview();
				        }
				        initPreview(width, height);
				        Parameters parameters = camera.getParameters();
				        Display display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

				        if(display.getRotation() == Surface.ROTATION_0)
				        {
				            parameters.setPreviewSize(display.getHeight(), display.getWidth());                           
				            camera.setDisplayOrientation(90);
				        }

				        if(display.getRotation() == Surface.ROTATION_90)
				        {
				            parameters.setPreviewSize(display.getWidth(), display.getHeight());  
				        }

				        if(display.getRotation() == Surface.ROTATION_180)
				        {
				            parameters.setPreviewSize(display.getHeight(), display.getWidth());               
				        }

				        if(display.getRotation() == Surface.ROTATION_270)
				        {
				            parameters.setPreviewSize(display.getWidth(), display.getHeight());
				            camera.setDisplayOrientation(180);
				        }

				        camera.setParameters(parameters);
				        startPreview();                      
				    }
			    
			    public void surfaceDestroyed(SurfaceHolder holder) {
			      // no-op
			    }
			  };
			  


    
}
