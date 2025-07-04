<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Http\Response;
use Illuminate\Support\Facades\Auth;
use App\Models\Usuario;

class AuthController extends Controller
{
    public function login(Request $request)
    {
        $credentials = $request->validate([
            'correo' => 'required|email',
            'contrasena' => 'required',
        ]);

        $usuario = \App\Models\Usuario::where('correo', $credentials['correo'])->first();

        if ($usuario && \Hash::check($credentials['contrasena'], $usuario->contrasena)) {
            $token = $usuario->createToken('token')->plainTextToken;
            return response()->json([
                'user' => $usuario,
                'token' => $token,
                'token_type' => 'Bearer',
            ], Response::HTTP_OK);
        } else {
            return response()->json([
                'message' => 'Credenciales incorrectas',
            ], Response::HTTP_UNAUTHORIZED);
        }
    }

    public function logout(Request $request)
    {
        $user = Auth::user();
        if ($user && $user->currentAccessToken()) {
            $user->tokens()->where('id', $user->currentAccessToken()->id)->delete();
        }
        return response()->json([
            'message' => 'Sesión cerrada exitosamente',
        ], Response::HTTP_OK);
    }
}