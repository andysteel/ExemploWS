package com.gmail.andersoninfonet.wsandroid;


import java.io.IOException;

import java.io.InputStream;

import org.apache.http.client.HttpResponseException;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


public class ExemploWSActivity extends Activity implements Runnable{
	
	private SoapObject soap;
	private String namespace = "http://www.oorsprong.org/websamples.countryinfo";
	private String METHOD_NAME = "CountryFlag";
	private SoapSerializationEnvelope envelope;
	private AndroidHttpTransport transporte;
	private String URL = "http://webservices.oorsprong.org/websamples.countryinfo/CountryInfoService.wso";
	private ProgressDialog progress;
	private SoapPrimitive resposta;
	private Drawable drawable;
	private String SOAP_ACTION = "http://webservices.oorsprong.org/websamples.countryinfo/CountryInfoService.wso?op=CountryFlag";
	private ImageView image;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		final EditText campo = (EditText) findViewById(R.id.txtCampo);
		Button botao = (Button) findViewById(R.id.botaoOK);
	    image = (ImageView) findViewById(R.id.imageView);
		
		
		
		botao.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				soap = new SoapObject(namespace, METHOD_NAME);
				
				PropertyInfo p1 = new PropertyInfo();
				p1.setName("sCountryISOCode");
				p1.setValue(campo.getText().toString());
				p1.setType(String.class);
				
				soap.addProperty(p1);
				
				
				envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
				//envelope.dotNet = true;
				
				envelope.setOutputSoapObject(soap);
				
				transporte = new AndroidHttpTransport(URL);
				
				progress = ProgressDialog.show(ExemploWSActivity.this, "carregando", "aguarde...",true, false);
				
				Thread thread = new Thread(ExemploWSActivity.this);
				thread.start();
			}
		});
	}

	@Override
	public void run() {
		try {
			transporte.call(SOAP_ACTION, envelope);
			resposta = (SoapPrimitive) envelope.getResponse();
			Log.d("resposta= ", resposta.toString());
			drawable = loadIMG(resposta.toString());
			handler.sendEmptyMessage(0);
		} catch (HttpResponseException e) {
			Log.d("erro= ", e.getMessage());
		} catch (IOException e) {
			Log.d("erro= ", e.getMessage());
		} catch (XmlPullParserException e) {
			Log.d("erro= ", e.getMessage());
		}
		
	}
	
	private Drawable loadIMG(String url){
		try{
			InputStream is = (InputStream) new java.net.URL(url).getContent();
			Log.d("url= ", url);
			Drawable d = Drawable.createFromStream(is, "src name");
			return d;
		}catch(Exception e){
			return null;
		}
	}
	
	public Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			progress.dismiss();
			image.setImageDrawable(drawable);
		}
	};
	
}
