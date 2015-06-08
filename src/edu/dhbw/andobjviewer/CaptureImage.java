package edu.dhbw.andobjviewer;

import java.io.File;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import edu.dhbw.andarmodelviewer.R;

public class CaptureImage extends Activity{
	
	protected Button _button;
	protected ImageView _image;
	protected TextView _field;
	protected String _path;
	protected boolean _taken;
	private ProgressDialog progressDialog;
	private String ocrData;
	private boolean validMonth=true;
	
	protected static final String PHOTO_TAKEN = "photo_taken";

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
	    super.onCreate(savedInstanceState);
	        
	    setContentView(R.layout.activity_capture_photo);
	       
	    _image = ( ImageView ) findViewById( R.id.image );
	    _field = ( TextView ) findViewById( R.id.field );
	    _button = ( Button ) findViewById( R.id.button );
	    _button.setOnClickListener( new ButtonClickHandler() );
	     _path = Environment.getExternalStorageDirectory() + "/ILPApp/snap.jpg";
	     
	     _image.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(_taken) {
					
					progressDialog = ProgressDialog.show(CaptureImage.this, "Please wait ...",  "We are extracting the data!", true);
	                progressDialog.setCancelable(true);
	                new Thread(new Runnable() {
	                    @Override
	                    public void run() {
	                        try {
	                        	OCRDataExtractor ocrExtractor = new OCRDataExtractor(_path);
	                        	ocrData = ocrExtractor.extractData().trim();
	                        	if(ocrData!=null)
	        					{
	                        		if(ocrData.contains("2015") && ocrData.toLowerCase().contains("tcs"))
	                        		{
	                        			String month = extractMonth(ocrData);
	                        			if(validMonth) {
			        						Intent intent = new Intent(CaptureImage.this,MagazineActivity.class);
			        						intent.putExtra("ocrData", ocrData);
			        						intent.putExtra("month", month);
			        						intent.putExtra("year", "2015");
			        						startActivity(intent);
	                        			}
	                        		}
	        					}
	                        } catch (Exception e) {
	                             
	                        }
	                        progressDialog.dismiss();
	                        
	                        // This code will enable running Toast in a thread...!!!
	                        (CaptureImage.this).runOnUiThread(new Runnable() {
	                            public void run() {
	                                Toast.makeText((CaptureImage.this), ocrData, Toast.LENGTH_LONG).show();
	                            }
	                        });
	                   }
	                }).start();
				}
				else {
					Toast.makeText(getApplicationContext(), "Capture image first!",
							Toast.LENGTH_SHORT).show();
				}
				
			}
		});
	}
	
	public class ButtonClickHandler implements View.OnClickListener 
	{
	    public void onClick( View view ){
	    	startCameraActivity();
	    }
	}
	
	protected void startCameraActivity()
	{
	    File file = new File( _path );
	    Uri outputFileUri = Uri.fromFile( file );
	    	
	    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE );
	    intent.putExtra( MediaStore.EXTRA_OUTPUT, outputFileUri );
	    	
	    startActivityForResult( intent, 0 );
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{	
	    Log.i( "MakeMachine", "resultCode: " + resultCode );
	    switch( resultCode )
	    {
	    	case 0:
	    		Log.i( "MakeMachine", "User cancelled" );
	    		break;
	    			
	    	case -1:
	    		onPhotoTaken();
	    		break;
	    }
	}
	
	protected void onPhotoTaken()
	{
	    _taken = true;
	    	
	    BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inSampleSize = 4;
	    	
	    Bitmap bitmap = BitmapFactory.decodeFile( _path, options );
	    _image.setImageBitmap(bitmap);
	    	
	    _field.setVisibility( View.GONE );
	}
	
	@Override
	protected void onSaveInstanceState( Bundle outState ) {
	    outState.putBoolean( CaptureImage.PHOTO_TAKEN, _taken );
	}
	
	@Override
	protected void onRestoreInstanceState( Bundle savedInstanceState)
	{
	    Log.i( "MakeMachine", "onRestoreInstanceState()");
	    if( savedInstanceState.getBoolean( CaptureImage.PHOTO_TAKEN ) ) {
	    	onPhotoTaken();
	    }
	}
	
	private String extractMonth(String data) {
		String formatedData = data.trim().toLowerCase();
		if(formatedData.contains("jan"))
		{
			return "January";
		}
		else if(formatedData.contains("feb"))
		{
			return "February";
		}
		else if(formatedData.contains("mar"))
		{
			return "March";
		}
		else if(formatedData.contains("apr"))
		{
			return "April";
		}
		else if(formatedData.contains("may"))
		{
			return "May";
		}
		else if(formatedData.contains("jun"))
		{
			return "June";
		}
		else if(formatedData.contains("jul"))
		{
			return "July";
		}
		else if(formatedData.contains("aug"))
		{
			return "August";
		}
		else if(formatedData.contains("sept"))
		{
			return "September";
		}
		else if(formatedData.contains("octo"))
		{
			return "October";
		}
		else if(formatedData.contains("nov"))
		{
			return "November";
		}
		else if(formatedData.contains("dec"))
		{
			return "December";
		}
		validMonth = false;
		return null;
	}
	

}
