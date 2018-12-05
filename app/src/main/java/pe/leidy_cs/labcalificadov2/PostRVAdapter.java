package pe.leidy_cs.labcalificadov2;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PostRVAdapter extends RecyclerView.Adapter<PostRVAdapter.ViewHolder> {

    private static final String TAG = PostRVAdapter.class.getSimpleName();

    private List<Post> posts;

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public PostRVAdapter(){
        this.posts = new ArrayList<>();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView nombre;
        TextView latLng;
        TextView correo;

        ViewHolder(View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nombre);
            latLng = itemView.findViewById(R.id.latLng);
            correo = itemView.findViewById(R.id.correo);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_post, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        final Post post = posts.get(position);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.d(TAG, "currentUser: " + currentUser);

        // Save/Update current user to Firebase Database
        User user = new User();

        viewHolder.nombre.setText(post.getNombre());
        viewHolder.latLng.setText(post.getLatLng());
        viewHolder.correo.setText(post.getEmail());

        // Obteniendo datos del usuario asociado al post (una vez, sin realtime)
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(post.getUserid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange " + dataSnapshot.getKey());
                User user = dataSnapshot.getValue(User.class);
                //viewHolder.nombre.setText(user.getDisplayName());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled " + databaseError.getMessage(), databaseError.toException());
            }
        });

        // Get currentuser from FirebaseAuth
        Log.d(TAG, "currentUser: " + currentUser);
    }

    @Override
    public int getItemCount() {
        return this.posts.size();
    }

}
