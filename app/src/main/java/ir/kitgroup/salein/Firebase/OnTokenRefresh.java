//package ir.kitgroup.salein.Firebase;
//
//import android.text.TextUtils;
//
//import com.google.firebase.iid.FirebaseInstanceId;
//import com.google.firebase.iid.FirebaseInstanceIdService;
//import com.orm.query.Select;
//
//import ir.kitgroup.salein.DataBase.Account;
//
//public class OnTokenRefresh extends FirebaseInstanceIdService {
//    @Override
//    public void onTokenRefresh() {
//        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//
//        try {
//            if (Select.from(Account.class).list().size() > 0)
//                if (!TextUtils.equals(Select.from(Account.class).first().Token, refreshedToken) && refreshedToken != null) {
//                    Account user = Select.from(Account.class).first();
//                    user.Token = refreshedToken;
//                    Account.save(user);
//                }
//        } catch (Exception ignored) {
//        }
//    }
//}