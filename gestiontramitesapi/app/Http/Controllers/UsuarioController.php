<?php

namespace App\Http\Controllers;

use App\Models\Usuario;
use Illuminate\Http\Request;
use Illuminate\Http\Response;

class UsuarioController extends Controller
{
    public function index()
    {
        $usuarios = Usuario::all();
        return response()->json($usuarios, Response::HTTP_OK);
    }

    public function store(Request $request)
    {
        $usuario = Usuario::create($request->all());
        return response()->json([
            'message' => 'Usuario creado exitosamente',
            'usuario' => $usuario
        ], Response::HTTP_CREATED);
    }

    public function show(Usuario $usuario)
    {
        return response()->json($usuario, Response::HTTP_OK);
    }

    public function update(Request $request, Usuario $usuario)
    {
        $usuario->update($request->all());
        return response()->json([
            'message' => 'Usuario actualizado exitosamente',
            'usuario' => $usuario
        ], Response::HTTP_OK);
    }

    public function destroy(Usuario $usuario)
    {
        $usuario->delete();
        return response()->json([
            'message' => 'Usuario eliminado exitosamente',
            'usuario' => $usuario->id_usuario
        ], Response::HTTP_OK);
    }
}