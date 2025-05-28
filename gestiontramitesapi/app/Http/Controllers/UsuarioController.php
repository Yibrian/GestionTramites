<?php

namespace App\Http\Controllers;

use App\Models\Usuario;
use Illuminate\Http\Request;
use Illuminate\Http\Response;
use Illuminate\Support\Facades\Validator;

class UsuarioController extends Controller
{
    
    private $rules = [
        'nombre' => 'required|string|max:255',
        'correo' => 'required|email|max:255|unique:usuarios,correo',
        'contrasena' => 'required|string|min:6|max:255',
    ];

    private $traductionAttributes = [
        'nombre' => 'nombre',
        'correo' => 'correo electrónico',
        'contrasena' => 'contraseña',
    ];

    
    public function applyValidator(Request $request, $isUpdate = false, $usuarioId = null)
    {
        $rules = $this->rules;
        
        if ($isUpdate && $usuarioId) {
            $rules['correo'] = 'required|email|max:255|unique:usuarios,correo,' . $usuarioId . ',id_usuario';
            $rules['contrasena'] = 'sometimes|string|min:6|max:255';
        }
        $validator = Validator::make($request->all(), $rules);
        $data = [];
        if ($validator->fails()) {
            $data = response()->json([
                'errors' => $validator->errors(),
                'data' => $request->all()
            ], Response::HTTP_BAD_REQUEST);
        }
        return $data;
    }

    public function index()
    {
        $usuarios = Usuario::all();
        return response()->json($usuarios, Response::HTTP_OK);
    }

    public function store(Request $request)
    {
        $data = $this->applyValidator($request);
        if (!empty($data)) {
            return $data;
        }
        $usuario = Usuario::create($request->all());
        return response()->json([
            'message' => 'Usuario creado exitosamente',
            'usuario' => $usuario
        ], Response::HTTP_CREATED);
    }

    public function show(Usuario $usuario)
    {
        $usuario->load('tramites');
        return response()->json($usuario, Response::HTTP_OK);
    }

    public function update(Request $request, Usuario $usuario)
    {
        $data = $this->applyValidator($request, true, $usuario->id_usuario);
        if (!empty($data)) {
            return $data;
        }
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