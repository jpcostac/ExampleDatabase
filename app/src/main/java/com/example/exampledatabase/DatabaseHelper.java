package com.example.exampledatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ContatosDB";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE contatos(id INTEGER PRIMARY KEY AUTOINCREMENT, nome TEXT, telefone TEXT, email TEXT)";

        String createTablePerfil ="CREATE TABLE perfil(id INTEGER PRIMARY KEY AUTOINCREMENT, nome TEXT, url TEXT, fkContato INTEGER, FOREIGN KEY (fkContato) REFERENCES contato(id))";

        db.execSQL(createTable);
        db.execSQL(createTablePerfil);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS contatos");
        db.execSQL("DROP TABLE IF EXISTS perfil");
        onCreate(db);
    }

    //Método para inserir um contato
    public boolean inserirContato(String nomeContato, String telefone, String email, String nomePerfil, String url){
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues values = new ContentValues();
        long resultado = 0;

        values.put("nome", nomeContato);
        values.put("telefone", telefone);
        values.put("email", email);

        try{
            db.insert("contatos", null, values);
            this.inserirPerfil(nomePerfil, url, id);
        }catch (SQLException e){
            Log.e("Erro",e.getMessage());
            resultado = -1;
        }finally {
            db.close();
        }

        return resultado != -1;
    }

    //Método para listar os contatos
    public Cursor listarContato(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM contatos", null);
    }

    //Método para atualizar um contato
    public boolean atualizarContato(int id, String nome, String telefone, String email){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("nome", nome);
        values.put("telefone", telefone);
        values.put("email", email);

        long resultado = db.update("contatos", values, "id=?", new String[]{String.valueOf(id)});
        db.close();

        return resultado > 0;
    }

    //Método para excluir um contato
    public boolean excluirContato(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        long resultado = db.delete("contatos", "id = ?", new String[]{String.valueOf(id)});

        db.close();
        return  resultado > 0;
    }

    //Método para listar os perfis de um contato
    public Cursor listarPerfis(int idContato){
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery("SELECT nome, url FROM perfil WHERE fkContato =?", new String[]{String.valueOf(idContato)});
    }

    //Método para listar os contatos com seus respectivos perfis
    public Cursor listarContatosComPerfil(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT c.id, c.nome, c.telefone, c.email, p.nome, p.url FROM contatos c" +
                " JOIN perfil p ON c.id = p.fkContato ORDER BY c.nome", null);
    }

    //Método para inserir um perfil
    public boolean inserirPerfil(String nome, String url, int idContato){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("nome", nome);
        values.put("url", url);
        values.put("fkContato", idContato);

        long resultado = db.insert("perfil", null, values);
        db.close();

        return resultado != -1;
    }
}