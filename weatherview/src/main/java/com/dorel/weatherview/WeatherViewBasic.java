package com.dorel.weatherview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherViewBasic extends RelativeLayout {

    /** Core Items*/
    private Context mContext;
    private AttributeSet attrs;
    private int styleAttr;
    private View view;

    private GpsTracker gpsService;

    public static String BaseUrl = "https://api.openweathermap.org/";
    public static String AppId = "26010db73e8c708ace1db9fa4dd392f9";
    public static double lat = 35;
    public static double lon = 139;

    /** Core Components*/
    ImageView image, weather_IMG_weather;


    TextView weather_TXT_city, weather_TXT_temp;
    View alphaLayer;

    /** Attributes **/
    Drawable imageFile;
    Drawable imagePlaceHolder;
    Drawable imageError;
    Drawable gradient;

    String region;


    public WeatherViewBasic(Context context) {
        super(context);

        this.mContext = context;
        initView();

    }

    public WeatherViewBasic(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.mContext = context;
        this.attrs = attrs;
        initView();

    }

    public WeatherViewBasic(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.mContext = context;
        this.attrs = attrs;
        this.styleAttr = defStyleAttr;
        initView();

    }

    private void initView() {
        this.view=this;
//Inflating the XML view
        inflate(mContext, R.layout.weather_view_layout,this);

        TypedArray arr = mContext.obtainStyledAttributes(attrs,R.styleable.WeatherViewBasic,
                styleAttr,0);

        imageFile=arr.getDrawable(R.styleable.WeatherViewBasic_imageSrc);
        imageError=arr.getDrawable(R.styleable.WeatherViewBasic_imageError);
        imagePlaceHolder=arr.getDrawable(R.styleable.WeatherViewBasic_imagePlaceholder);
        gradient=arr.getDrawable(R.styleable.WeatherViewBasic_gradient);
        region = arr.getString(R.styleable.WeatherViewBasic_region);
//components
        weather_IMG_weather=(ImageView)findViewById(R.id.weather_IMG_weather);
        alphaLayer= findViewById(R.id.gradient);

        weather_TXT_city = findViewById(R.id.weather_TXT_city);
        weather_TXT_temp = findViewById(R.id.weather_TXT_temp);

        if(imageFile!=null) {
            setDrawableImage(imageFile);
        }

        if(gradient!=null){
            alphaLayer.setBackground(gradient);
        }

        // * Start of Location Service
        gpsService = new GpsTracker(mContext);
        if (gpsService.canGetLocation()) {
            lat = gpsService.getLatitude();
            lon = gpsService.getLongitude();
        } else {
            gpsService.showSettingsAlert();
        }
        Log.d("pttt", "lat:" + lat + "long" + lon);

        getCurrentData();
        // * End of Location Service

        arr.recycle();
    }

    public void setScaleType(ImageView.ScaleType scaleType){
        image.setScaleType(scaleType);
    }

    public void setGradient(Drawable gradient){
        alphaLayer.setBackground(gradient);
    }

    public void setDrawableImage(Drawable imageFile){
        image.setImageDrawable(imageFile);
    }

    public void setDrawableImage(int imageFile, int imageError, int imagePlaceHolder,
                                 ImageView.ScaleType scaleType) {

        image.setScaleType(scaleType);
        Glide
                .with(mContext)
                .load(imageFile)
                .placeholder(imagePlaceHolder)
                .error(imageError)
                .into(image);

    }

    public void setUrlImage(String url, int imageError, int imagePlaceHolder,
                            ImageView.ScaleType scaleType) {
        weather_IMG_weather.setScaleType(scaleType);
        Glide
                .with(mContext)
                .load(url)
                .placeholder(imagePlaceHolder)
                .dontAnimate()
                .error(imageError)
                .into(weather_IMG_weather);

    }

    public void setResImage(int resID,ImageView.ScaleType scaleType) {
        image.setScaleType(scaleType);
        image.setImageResource(resID);

    }

    void getCurrentData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        WeatherService service = retrofit.create(WeatherService.class);

        Double latitudeInt = (double) lat;
        Double longitudeInt = (double) lon;
        Log.v("pttt", String.valueOf("Response: " + latitudeInt + longitudeInt));


        Call<WeatherResponse> call = service.getCurrentWeatherData(String.valueOf(latitudeInt), String.valueOf(longitudeInt), AppId);
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                Log.v("pttt", String.valueOf("Response: " + response.code()));
                if(response.code() == 200){
                    WeatherResponse weatherResponse = response.body();
                    assert weatherResponse != null;

                    weather_TXT_city.setText("" + weatherResponse.name);

                    int tempINTval = (int) weatherResponse.main.temp;
                    weather_TXT_temp.setText("" + tempINTval + "°");

                    String ic = weatherResponse.weather.get(0).icon;

                    setUrlImage("https://openweathermap.org/img/w/" + ic + ".png",R.drawable.weather,
                            R.drawable.weather, ImageView.ScaleType.CENTER_CROP);
                    Log.d("pttt", "" + weatherResponse.main.temp);
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.d("ptttt", t.getMessage());

            }
        });
    }
}
