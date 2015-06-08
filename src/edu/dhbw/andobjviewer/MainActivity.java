package edu.dhbw.andobjviewer;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import edu.dhbw.andarmodelviewer.R;
import edu.dhbw.andobjviewer.sqllite.DatabaseHandler;
import edu.dhbw.andobjviewer.sqllite.Magazine;
import edu.dhbw.andobjviewer.sqllite.MagazineContent;

public class MainActivity extends Activity{
	
	private SurfaceView videoView;
	private SurfaceHolder videoHolder;
	private Camera camera;
	private Button btnObject;
	private Button btnBook;
	private boolean inPreview=false;
	private boolean cameraConfigured=false;

	@Override
	public void onCreate(Bundle savedInstanceState){
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		videoView = (SurfaceView) findViewById(R.id.video_view);
		btnObject = (Button) findViewById(R.id.btnObject);
		btnBook = (Button) findViewById(R.id.btnBook);
		videoHolder = videoView.getHolder();
		videoHolder.addCallback(surfaceCallback);
		videoHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		btnObject.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				btnObject.setTextColor(Color.RED);
				Intent intent = new Intent(getBaseContext(), ModelChooser.class);
	            startActivity(intent);
				btnObject.setTextColor(Color.WHITE);
				/*Intent intent = new Intent(getBaseContext(),ModelChooser.class);
				startActivity(intent);*/
			}
		});
		
		btnBook.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				btnBook.setTextColor(Color.RED);
				Intent intent = new Intent(getBaseContext(), CaptureImage.class);
				startActivity(intent);
				btnBook.setTextColor(Color.WHITE);
				
			}
		});
		
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
		          .makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG)
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