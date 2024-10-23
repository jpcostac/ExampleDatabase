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
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableContato = "CREATE TABLE contatos(id INTEGER PRIMARY KEY AUTOINCREMENT,nome TEXT, telefone TEXT,email TEXT)";

        String createTablePerfil = "CREATE TABLE perfil(id INTEGER PRIMARY KEY AUTOINCREMENT,nome TEXT, url TEXT, fkContato INTEGER, FOREIGN KEY(fkContato)REFERENCES contatos(id))";

        db.execSQL(createTableContato);
        db.execSQL(createTablePerfil);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS contatos");
        db.execSQL("DROP TABLE IF EXISTS perfil");
        onCreate(db);
    }


    //METODO PARA INSERIR UM CONTATO
    public boolean inserirContato(String nomeContato, String telefone, String email,String nomePerfil, String url){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        long resultado = 0;
        values.put("nome", nomePerfil);
        values.put("email",email);
        values.put("url", url);

        try{
            int id = (int) db.insert("contatos",null,values);
            if(id==-1 && this.inserirPerfil(nomePerfil, url, id) == false) {
                resultado=-1;
            }
        } catch (SQLException e){
            Log.e("Erro",e.getMessage());
        }finally {
            db.close();
        }
        return resultado != -1;
    }

    
    //METODO PARA LISTAR OS CONTATOS
    public Cursor listarContatos(){
        SQLiteDatabase db = this.getWritableDatabase();

        return db.rawQuery("SELECT * FROM contatos", null);
    }


    //METODO PARA ATUALIZAR UM CONTATO
    public boolean atualizarContato(int id, String nome, String telefone, String email,int idPerfil, String nomePerfil, String url) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("nome", nome);
        values.put("telefone", telefone);
        values.put("email", email);

        try {
            if (db.update("contatos", values, "id=?", new String[]{String.valueOf(id)}) == 0) {
                return false;
            }
            this.atualizarPerfil(idPerfil, nomePerfil, url);
        } catch (SQLException e) {
            Log.e("Erro", e.getMessage());
            return false;
        } finally {
            db.close();
        }
        return true;
    }


    //METODO PARA EXCLUIR CONTATO
    public boolean excluirContato(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            if(db.delete("contatos", "id=?",new String[]{String.valueOf(id)}) == 0 && this.excluirPerfil(id) == false);
        }catch(SQLException e){
            Log.e("Erro", e.getMessage());
        }finally {
            db.close();
        }
        return true;
    }


    //Método para Listar os perfis de um contato
    public Cursor listarPerfis(int idContato){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT nome, url FROM perfil WHERE fkContato=?",new String[]{String.valueOf(idContato)});
    }


    //Métodos para listar os contatos com seus respectivos perfis
    public Cursor listarContatoComPerfis(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT c.id, c.nome, c.telefone, c.email, p.nome, p.url FROM contatos c JOIN perfil p ON c.id = p.fkContato ORDER BY c.nome", null);
    }


    //Método para inserir um Perfil
    private boolean inserirPerfil(String nome, String url, int idContato){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("nome", nome);
        values.put("url",url);
        values.put("fkContato",idContato);

        long resultado = db.insert("perfil",null,values);

        db.close();

        return resultado != -1;
    }


    //Método para atualizar o perfil de um contato
    private boolean atualizarPerfil(int id,String nome, String url) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("nome", nome);
        values.put("url", url);

        try {
            if(db.update("contatos", values, "id=?", new String[]{String.valueOf(id)}) == 0){
                return false;
            }
        } catch (SQLException e) {
            Log.e("Erro", e.getMessage());
            return false;
        } finally {
            db.close();
        }
        return true;
    }


    //METODO PARA EXCLUIR O PERFIL
    public boolean excluirPerfil(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            if(db.delete("perfil","fkContato=?", new String[]{String.valueOf(id)})== 0) return false;
        }catch (SQLException e){
            Log.e("Erro", e.getMessage());
        }finally {
            db.close();
        }
        return true;
    }
}