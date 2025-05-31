<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\UsuarioController;
use App\Http\Controllers\TipoTramiteController;
use App\Http\Controllers\TramiteController;
use App\Http\Controllers\RequisitoController;
use App\Http\Controllers\AuthController;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider and all of them will
| be assigned to the "api" middleware group. Make something great!
|
*/

Route::middleware('auth:sanctum')->get('/user', function (Request $request) {
    return $request->user();
});


Route::post('auth/login', [AuthController::class, 'login'])->name('auth.login');


Route::middleware('auth:sanctum')->group(function () {
    Route::post('auth/logout', [AuthController::class, 'logout'])->name('auth.logout');
    Route::apiResource('usuarios', UsuarioController::class);
    Route::apiResource('tipo-tramites', TipoTramiteController::class);
    Route::apiResource('tramites', TramiteController::class);
    Route::apiResource('requisitos', RequisitoController::class);
});
