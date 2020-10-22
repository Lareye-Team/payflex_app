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
import orderFlex.paymentCollection.Model.APILog.APILogData;
import orderFlex.paymentCollection.Model.DataBase.DatabaseOperation;
import orderFlex.paymentCollection.Model.PaymentAndBillData.BillPaymentRequestBody;
import orderFlex.paymentCollection.Model.PaymentAndBillData.BillPaymentResponse;
import orderFlex.paymentCollection.Model.PaymentAndBillData.UpdatePaymenResponse;
import orderFlex.paymentCollection.Utility.Constant;
import orderFlex.paymentCollection.Utility.Helper;
import orderFlex.paymentCollection.Utility.SharedPrefManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UpdateBill {
    private String TAG="LoginAPICalling";
    private APIinterface apIinterface;
    private Gson gson;
    private UpdateBillListener listener;
    private Context context;
    private ProgressDialog dialog;
    private UpdatePaymenResponse updatePaymentResponse=null;
    private DatabaseOperation db;
    private APILogData logData=new APILogData();

    public UpdateBill(Context context) {
        listener= (UpdateBillListener) context;
        this.context=context;
        db=new DatabaseOperation(context);
    }

    public void updateBillCall(final String username, final String password, BillPaymentRequestBody body){
        // preparing interceptor for retrofit
        // interceptor for runtime data checking
        dialog = new ProgressDialog(context);
        dialog.setMessage("Update today's bills ...");
        dialog.show();
        //////////////log operation///////////
        logData.setCallName("Update Payment");
        logData.setCallURL(Constant.BASE_URL_PAYFLEX+"UpdatePaymentData");
        logData.setCallTime(new Helper(context).getDateTimeInEnglish());
        logData.setRequestBody(new Gson().toJson(body));
        logData.setResponseCode("");
        logData.setResponseBody("");
        logData.setException("");
        /////////////////////////////////
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
        final Call<UpdatePaymenResponse> billPaymentResponseCall=apIinterface.updateOrderPayment(body);
        billPaymentResponseCall.enqueue(new Callback<UpdatePaymenResponse>() {
            @Override
            public void onResponse(Call<UpdatePaymenResponse> call, retrofit2.Response<UpdatePaymenResponse> response) {
                //////////////log operation///////////
                if (new SharedPrefManager(context).isDebugOn()){
                    logData.setResponseCode(String.valueOf(response.code()));
                    logData.setResponseBody(new Gson().toJson(response.body()));
                    logData.setResponseTime(new Helper(context).getDateTimeInEnglish());
                    db.insertAPILog(logData);
                }
                ///////////////////////////////////
                if (response.isSuccessful()){
                    updatePaymentResponse=response.body();
                    gson=new Gson();
                    String res= gson.toJson(updatePaymentResponse);
                    Log.i(TAG,"Update Bill Response: "+res);
                    listener.onUpdateResponse(updatePaymentResponse,response.code());
                    dialog.cancel();
                }
                dialog.cancel();
            }
            @Override
            public void onFailure(Call<UpdatePaymenResponse> call, Throwable t) {
                if (new SharedPrefManager(context).isDebugOn()){
                    logData.setException(t.getMessage());
                    db.insertAPILog(logData);
                }
                listener.onUpdateResponse(updatePaymentResponse,404);
                dialog.cancel();
            }
        });
        return;
    }

    public interface UpdateBillListener{
        void onUpdateResponse(UpdatePaymenResponse response,int code);
    }
}
