package orderFlex.paymentCollection.Model.APICallings;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Credentials;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import orderFlex.paymentCollection.Model.TodayOrder.TodayOrderedProductByCodeRequest;
import orderFlex.paymentCollection.Model.TodayOrder.TodayOrderResponse;
import orderFlex.paymentCollection.Utility.Constant;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PullOrderDetailsByOrderCode {
    private String TAG="PullOrderDetailsByOrderCode";
    private APIinterface apIinterface;
    private Gson gson;
    private TodayOrderListener listener;
    private Context context;
    private ProgressDialog dialog;
    private TodayOrderResponse todayOrderResponse=null;


    public PullOrderDetailsByOrderCode(Context context) {
        listener= (TodayOrderListener) context;
        this.context=context;
    }

    public void pullOrderCall(final String username, final String password, TodayOrderedProductByCodeRequest orderRequest){
        // preparing interceptor for retrofit
        // interceptor for runtime data checking
//        dialog = new ProgressDialog(context);
//        dialog.setMessage("Updating...");
//        dialog.show();
        Log.i(TAG,"Pull Order details called");
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        final String authToken = Credentials.basic(username, password);
        OkHttpClient okHttpClient=new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        Headers headers = request.headers().newBuilder().add("Authorization", authToken).build();
                        request = request.newBuilder().headers(headers).build();
                        return chain.proceed(request);
                    }
                })
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL_PAYFLEX)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        apIinterface=retrofit.create(APIinterface.class);
        final Call<TodayOrderResponse> orderResponseCall = apIinterface.getOrderByCode(orderRequest);
        orderResponseCall.enqueue(new Callback<TodayOrderResponse>() {
            @Override
            public void onResponse(Call<TodayOrderResponse> call, retrofit2.Response<TodayOrderResponse> response) {
                Log.i(TAG,"Code: "+response.code());
                if (response.isSuccessful()){
                    todayOrderResponse=response.body();
                    gson=new Gson();
                    String res= gson.toJson(todayOrderResponse);
                    Log.i(TAG,"Login Response: "+res);
                    listener.onResponse(todayOrderResponse,response.code());
//                    dialog.cancel();
                }
            }
            @Override
            public void onFailure(Call<TodayOrderResponse> call, Throwable t) {
                Log.i(TAG,"Exception: "+t.getMessage());
                listener.onResponse(todayOrderResponse,404);
//                dialog.cancel();
            }
        });
        return;
    }

    public interface TodayOrderListener{
        void onResponse(TodayOrderResponse response,int code);
    }
}
