package AppBD;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import Placas.Placa;

public class BD {
    public static ArrayList<Placa> placas = new ArrayList();
    public static int cont = 0;
    private static DatabaseReference databaseReference;
    private static FirebaseDatabase firebaseDatabase;

    public static DatabaseReference getFirebase() {
        if (databaseReference == null) {
            firebaseDatabase = FirebaseDatabase.getInstance();
            firebaseDatabase.setPersistenceEnabled(true);
            databaseReference = firebaseDatabase.getReference();
        }
        return databaseReference;
    }
}
