package com.example.pokedexapp.activities;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.pokedexapp.R;
import com.example.pokedexapp.controllers.RetrofitConfig;
import com.example.pokedexapp.models.Pokemon;
import com.example.pokedexapp.models.Usuario;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {
    Usuario user;
    Pokemon pokemon;
    ImageView imageViewDetalheFoto;
    EditText editTextDetalheNome, editTextDetalheTipo,
            editTextDetalheHabilidade1, editTextDetalheHabilidade2, editTextDetalheHabilidade3;
    TextView textViewCadastradoPor;

    private Uri selectedImage;
    String imagem;

    private static final int PICK_IMAGE_REQUEST = 1307;
    private static final int REQUEST_EXTERNAL_STORAGE = 0402;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        user = (Usuario) intent.getSerializableExtra("usuario");
        pokemon = (Pokemon) intent.getSerializableExtra("pokemon");

        editTextDetalheNome = findViewById(R.id.editTextDetalheNome);
        imageViewDetalheFoto = findViewById(R.id.imageViewDetalheFoto);
        editTextDetalheTipo = findViewById(R.id.editTextDetalheTipo);
        editTextDetalheHabilidade1 = findViewById(R.id.editTextDetalheHabilidade1);
        editTextDetalheHabilidade2 = findViewById(R.id.editTextDetalheHabilidade2);
        editTextDetalheHabilidade3 = findViewById(R.id.editTextDetalheHabilidade3);
        editTextDetalheNome.setText(pokemon.getNome());
        textViewCadastradoPor = findViewById(R.id.textViewCadastradoPor);
        getNomeUsuario(pokemon.getId());

        for (String habilidade : pokemon.getHabilidade()) {
            if (editTextDetalheHabilidade1.length() == 0) {
                editTextDetalheHabilidade1.setText(habilidade);
            } else {
                if (editTextDetalheHabilidade2.length() == 0) {
                    editTextDetalheHabilidade2.setText(habilidade);
                } else {
                    if (editTextDetalheHabilidade3.length() == 0) {
                        editTextDetalheHabilidade3.setText(habilidade);
                    }
                }
            }
        }


        Bitmap image = byteToBitmap(pokemon.getFoto());
        imageViewDetalheFoto.setImageBitmap(image);

        imageViewDetalheFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyStoragePermissions(DetailActivity.this);
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Abrir galeria"), PICK_IMAGE_REQUEST);
            }
        });
    }

    public static Bitmap byteToBitmap(byte[] bytes) {
        return (bytes == null || bytes.length == 0) ? null : BitmapFactory
                .decodeByteArray(bytes, 0, bytes.length);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Bitmap bitmap = null;
            try {
                selectedImage = data.getData();
                Cursor cursor = getContentResolver().query(selectedImage, null, null, null, null);
                cursor.moveToFirst();
                String documentId = cursor.getString(0);
                documentId = documentId.substring(documentId.lastIndexOf(":") + 1);
                cursor.close();

                cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media._ID + " = ?", new String[]{documentId}, null);
                cursor.moveToFirst();
                imagem = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                cursor.close();
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
            } catch (IOException e) {

            }
            imageViewDetalheFoto.setImageBitmap(bitmap);
        }

    }

    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public void salvarAlteracoes(View view) {
        if (editTextDetalheNome.length() == 0) {
            Toast.makeText(this, "Nome não pode ser vazio!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (editTextDetalheTipo.length() == 0) {
            Toast.makeText(this, "Tipo não pode ser vazio!", Toast.LENGTH_SHORT).show();
            return;
        }
        if ((editTextDetalheHabilidade1.length() == 0) && (editTextDetalheHabilidade2.length() == 0) && (editTextDetalheHabilidade3.length() == 0)) {
            Toast.makeText(this, "Informe ao menos uma habilidade!", Toast.LENGTH_SHORT).show();
            return;
        }

        pokemon.setNome(editTextDetalheNome.getText().toString());
        pokemon.setTipo(editTextDetalheTipo.getText().toString());
        List<String> habilidades = new ArrayList<>();
        habilidades.add(editTextDetalheHabilidade1.getText().toString());
        habilidades.add(editTextDetalheHabilidade2.getText().toString());
        habilidades.add(editTextDetalheHabilidade3.getText().toString());
        pokemon.setHabilidade(habilidades);
        File file = new File(imagem);
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        ByteArrayOutputStream blob = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, blob);
        byte[] bitmapdata = blob.toByteArray();
        pokemon.setFoto(bitmapdata);
        pokemon.setUsuario(user.getId());

        Gson gson = new Gson();
        String jsonPokemon = gson.toJson(pokemon);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonPokemon);

        Call<Pokemon> cadastrarPokemon = new RetrofitConfig().getPokemonsService().atualizarPokemon(pokemon.getId(), requestBody);
        cadastrarPokemon.enqueue(new Callback<Pokemon>() {
            @Override
            public void onResponse(Call<Pokemon> call, Response<Pokemon> response) {
                if (response.isSuccessful()) {
                    new AlertDialog.Builder(DetailActivity.this)
                            .setTitle("Sucesso")
                            .setMessage("Pokemon alterado com sucesso")
                            .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    recarregaLista();
                                }
                            })
                            .show();
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        new AlertDialog.Builder(DetailActivity.this).setTitle("Erro").setMessage(jObjError.getString("error")).show();
                    } catch (JSONException | IOException e) {
                        new AlertDialog.Builder(DetailActivity.this).setTitle("Erro").setMessage("Erro interno.").show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Pokemon> call, Throwable t) {
                Log.e("ERRO", "Cadastrar Pokemon: " + t.getMessage());
                new AlertDialog.Builder(DetailActivity.this).setTitle("Erro interno").setMessage("Erro ao conectar ao servidor.").show();
            }
        });
    }

    public void excluirPokemon(View view) {
        Call<ResponseBody> pokemonDeleteCall = new RetrofitConfig().getPokemonsService().deletarPokemon(pokemon.getId());
        pokemonDeleteCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    new AlertDialog.Builder(DetailActivity.this)
                            .setTitle("Sucesso")
                            .setMessage("Pokemon removido com sucesso!")
                            .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    recarregaLista();
                                }
                            })
                            .show();
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        new AlertDialog.Builder(DetailActivity.this).setTitle("Erro").setMessage(jObjError.getString("error")).show();
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        new AlertDialog.Builder(DetailActivity.this).setTitle("Erro").setMessage(response.errorBody().toString()).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("ERRO", "DeletarPokemon: " + t.getMessage());
                new AlertDialog.Builder(DetailActivity.this).setTitle("Erro!").setMessage(t.getMessage()).show();
            }
        });

    }

    private void recarregaLista() {
        Call<List<Pokemon>> callPokemons = new RetrofitConfig().getPokemonsService().getPokemons();
        callPokemons.enqueue(new Callback<List<Pokemon>>() {
            @Override
            public void onResponse(Call<List<Pokemon>> call, Response<List<Pokemon>> response) {
                if (response.isSuccessful()) {
                    List<Pokemon> pokemonList = new ArrayList<>(response.body());
                    Intent intent;
                    if (pokemonList.size() == 0) {
                        intent = new Intent(DetailActivity.this, DashboardActivity.class);
                    } else {
                        intent = new Intent(DetailActivity.this, ListaActivity.class);
                    }
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("pokemons", (Serializable) pokemonList);
                    bundle.putSerializable("usuario", user);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        new AlertDialog.Builder(DetailActivity.this).setTitle("Erro").setMessage(jObjError.getString("error")).show();
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        new AlertDialog.Builder(DetailActivity.this).setTitle("Erro").setMessage(response.errorBody().toString()).show();

                    }
                }
            }

            @Override
            public void onFailure(Call<List<Pokemon>> call, Throwable t) {
                Log.e("ERRO", "getPokemons: " + t.getMessage());
                new AlertDialog.Builder(DetailActivity.this).setTitle("Erro!").setMessage(t.getMessage()).show();
            }
        });
    }

    private void getNomeUsuario(Long id){
        Call<Usuario> callUsuario = new RetrofitConfig().loginService().getUsuarioById(id);
        callUsuario.enqueue(new Callback<Usuario>() {

            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {
                    Usuario usuario = response.body();
                    textViewCadastradoPor.setText("Cadastrado por " + usuario.getLogin());
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                    Log.e("ERRO", "getNomeUsuario: " + t.getMessage());
            }
        });
    }

}