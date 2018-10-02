package plus.life.pabb.com.lifeplus6;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.EditText;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Iterator;

public class Medicamentos extends AppCompatActivity{
    Button button5;

    // Base de datos real
    private ListView dataListView;
    private EditText itemText;
    private Button findButton;
    private Button deleteButton;
    private Button addButton;
    private Boolean searchMode = false;
    private Boolean itemSelected = false;
    private int selectedPosition = 0;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference("Medicamentos");
    //

    //Listview
    ArrayList<String> listItems = new ArrayList<String>();
    ArrayList<String> listKeys = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    //

    //addchild
    private void addChildEventListener() {
        ChildEventListener childListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                adapter.add(
                        (String) dataSnapshot.child("medicamento").getValue());

                listKeys.add(dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();
                int index = listKeys.indexOf(key);

                if (index != -1) {
                    listItems.remove(index);
                    listKeys.remove(index);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        dbRef.addChildEventListener(childListener);
    }
    //

    //add Item
    public void addItem(View view) {

        String item = itemText.getText().toString();
        String key = dbRef.push().getKey();

        itemText.setText("");
        dbRef.child(key).child("medicamento").setValue(item);

        adapter.notifyDataSetChanged();
    }
    //

    //Delete Item
    public void deleteItem(View view) {
        dataListView.setItemChecked(selectedPosition, false);
        dbRef.child(listKeys.get(selectedPosition)).removeValue();
    }
    //

    //Query
    public void findItems(View view) {

        Query query;

        if (!searchMode) {
            findButton.setText("Limpiar");
            query = dbRef.orderByChild("Medicamentos").
                    equalTo(itemText.getText().toString());
            searchMode = true;
        } else {
            searchMode = false;
            findButton.setText("Encontrar");
            query = dbRef.orderByKey();
        }

        if (itemSelected) {
            dataListView.setItemChecked(selectedPosition, false);
            itemSelected = false;
            deleteButton.setEnabled(false);
        }

        query.addListenerForSingleValueEvent(queryValueListener);
    }
    //

    ValueEventListener queryValueListener = new ValueEventListener() {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
            Iterator<DataSnapshot> iterator = snapshotIterator.iterator();

            adapter.clear();
            listKeys.clear();

            while (iterator.hasNext()) {
                DataSnapshot next = (DataSnapshot) iterator.next();

                String match = (String) next.child("Medicamentos").getValue();
                String key = next.getKey();
                listKeys.add(key);
                adapter.add(match);
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medi_camentos);
        button5 = (Button) findViewById(R.id.button5);

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent I = new Intent(Medicamentos.this, UserActivity.class);
                startActivity(I);

            }
        });

        dataListView = (ListView) findViewById(R.id.dataListView);
        itemText = (EditText) findViewById(R.id.itemText);
        addButton = (Button) findViewById(R.id.addButton);
        findButton = (Button) findViewById(R.id.findButton);
        deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setEnabled(false);

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_single_choice,
                listItems);
        dataListView.setAdapter(adapter);
        dataListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        dataListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent,
                                            View view, int position, long id) {
                        selectedPosition = position;
                        itemSelected = true;
                        deleteButton.setEnabled(true);
                    }
                });

        addChildEventListener();

    }

}
