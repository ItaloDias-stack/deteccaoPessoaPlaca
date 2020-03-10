package Placas;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;
import AppBD.BD;

public class Placa {
    private String id;
    private String placa;


    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void salvar() {
        DatabaseReference databaseReference = BD.getFirebase();
        databaseReference.child("placas").child(String.valueOf(getId())).setValue(this);
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> hashMapPlaca = new HashMap<>();
        hashMapPlaca.put("id", getId());
        hashMapPlaca.put("placa", getPlaca());
        return hashMapPlaca;
    }

    @Override
    public String toString() {
        return "Placa: "+getPlaca();
    }
}
