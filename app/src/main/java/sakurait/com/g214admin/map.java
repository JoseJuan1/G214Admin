package sakurait.com.g214admin;

import android.location.Geocoder;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.MinimapOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class map extends AppCompatActivity {
    private MapView map;
    private MapController mapC;
    Marker marker;
    ArrayList<OverlayItem> puntos;
    MyLocationNewOverlay myLocationoverlay;

    Geocoder geocoder;

    String direccion;
    Double lat;
    Double lng;
    String Estado;
    String Municipio;
    String Localidad;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_map );

        FloatingActionButton fab = (FloatingActionButton) findViewById( R.id.fab );
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make( view, "Replace with your own action", Snackbar.LENGTH_LONG ).setAction( "Action", null ).show();
                Toast.makeText( getApplicationContext(),"Enviar datos por intente a otra pantalla\n LAT: "+lat+"\n LON: "+lng,Toast.LENGTH_SHORT ).show();
            }
        } );

        FloatingActionButton fab2 = (FloatingActionButton) findViewById( R.id.fab2 );
        fab2.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make( view, "Replace with your own action", Snackbar.LENGTH_LONG ).setAction( "Action", null ).show();
                //recreate();
                CreateMap();
            }
        } );

        CreateMap();

    }

    public void CreateMap(){
        GeoPoint xalapa= new GeoPoint( 19.5420361,-96.9549487 );
        /*GeoPoint oco= new GeoPoint( 19.2699068,-99.4728757 );
        GeoPoint xala= new GeoPoint( 19.1797622,-99.4287866 );*/

        map=(MapView) findViewById( R.id.openmapview );
        map.setBuiltInZoomControls( true );
        mapC=(MapController) map.getController();
        mapC.setCenter( xalapa );
        mapC.setZoom( 20 );

        map.setMultiTouchControls( true );

        /*puntos = new ArrayList<OverlayItem>();
        puntos.add(new OverlayItem("Madrid", "Ciudad de Madrid", xalapa));
        puntos.add(new OverlayItem("Ocoyoacac", "Ciudad de Madrid2", oco));
        puntos.add(new OverlayItem("Xalatlaco", "Ciudad de Madrid2", xala));

        ItemizedIconOverlay.OnItemGestureListener<OverlayItem> tap = new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
            @Override
            public boolean onItemLongPress(int arg0, OverlayItem arg1) {
                return false;
            }
            @Override
            public boolean onItemSingleTapUp(int index, OverlayItem item) {
                return true;
            }
        };

        ItemizedOverlayWithFocus<OverlayItem> capa = new ItemizedOverlayWithFocus<OverlayItem>(this, puntos, tap);
        capa.setFocusItemsOnTap(true);
        map.getOverlays().add(capa);
        //capa.setFocusedItem(0);*/


        myLocationoverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getApplicationContext()), map);
        //map.getOverlays().add(myLocationoverlay); //No añadir si no quieres una marca
        map.getOverlays().clear();
        myLocationoverlay.enableMyLocation();
        myLocationoverlay.runOnFirstFix(new Runnable() {
            public void run() {
                mapC.animateTo(myLocationoverlay.getMyLocation());
                marker= new Marker( map );
                marker.setPosition(myLocationoverlay.getMyLocation());
                marker.setDraggable(true);
                marker.setIcon( getResources().getDrawable( R.drawable.person ));
                marker.setOnMarkerDragListener( new Marker.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDrag(Marker marker) {
                    }
                    @Override
                    public void onMarkerDragEnd(Marker marker) {
                        System.out.println("new marker-> "+marker.getPosition());
                        direccion=Geocoder(marker.getPosition().getLatitude(),marker.getPosition().getLongitude());
                        UpdateTexts();
                    }
                    @Override
                    public void onMarkerDragStart(Marker marker) {
                    }
                } );
                map.getOverlays().add( marker );

                marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker, MapView mapView) {
                        marker.setTitle(direccion+"\n"+"LAT: "+lat+"\nLNG: "+lng+"\nEstado: "+Estado+"\nMunicipio: "+Municipio+"\nLocalidad:"+Localidad);
                        marker.showInfoWindow();
                        return true;
                    }
                });
                direccion=Geocoder(marker.getPosition().getLatitude(),marker.getPosition().getLongitude());
                UpdateTexts();
            }
        });




        MinimapOverlay miniMapOverlay = new MinimapOverlay(this, map.getTileRequestCompleteHandler());
        miniMapOverlay.setZoomDifference(0);
        miniMapOverlay.setHeight(200);
        miniMapOverlay.setWidth(200);
        map.getOverlays().add(miniMapOverlay);

        ScaleBarOverlay mScaleBarOverlay = new ScaleBarOverlay(map);
        mScaleBarOverlay.setCentred(true);
        //play around with these values to get the location on screen in the right place for your application
        final DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);
        map.getOverlays().add(mScaleBarOverlay);

        map.isAnimating();
    }

    public String Geocoder(Double lat,Double lng){
        String direccion = null;
        geocoder= new Geocoder( getApplicationContext(), Locale.getDefault() );
        try {
            direccion=geocoder.getFromLocation( lat,lng,1 ).get( 0 ).getAddressLine( 0 );
            System.out.println("here!");
            System.out.println(geocoder.getFromLocation( lat,lng,1 ).get( 0 ));
            Estado=geocoder.getFromLocation( lat,lng,1 ).get( 0 ).getAdminArea();
            Municipio=geocoder.getFromLocation( lat,lng,1 ).get( 0 ).getSubAdminArea();
            if (Municipio==null||Municipio.equals( "null" )){
                Municipio=geocoder.getFromLocation( lat,lng,1 ).get( 0 ).getLocality();
            }
            Localidad=geocoder.getFromLocation( lat,lng,1 ).get( 0 ).getSubLocality();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return direccion;
    }

    public void UpdateTexts(){
        lat=marker.getPosition().getLatitude();
        lng=marker.getPosition().getLongitude();
        Estado=Estado;
        Municipio=Municipio;
        Localidad=Localidad;
    }

}
