<?php

namespace App\Http\Controllers;

use App\Models\Requisito;
use Illuminate\Http\Request;
use Illuminate\Http\Response;

class RequisitoController extends Controller
{
    public function index()
    {
        $requisitos = Requisito::with('tramite')->get();
        return response()->json($requisitos, Response::HTTP_OK);
    }

    public function store(Request $request)
    {
        $requisito = Requisito::create($request->all());
        return response()->json([
            'message' => 'Requisito creado exitosamente',
            'requisito' => $requisito
        ], Response::HTTP_CREATED);
    }

    public function show(Requisito $requisito)
    {
        $requisito->load('tramite');
        return response()->json($requisito, Response::HTTP_OK);
    }

    public function update(Request $request, Requisito $requisito)
    {
        $requisito->update($request->all());
        return response()->json([
            'message' => 'Requisito actualizado exitosamente',
            'requisito' => $requisito
        ], Response::HTTP_OK);
    }

    public function destroy(Requisito $requisito)
    {
        $requisito->delete();
        return response()->json([
            'message' => 'Requisito eliminado exitosamente',
            'requisito' => $requisito->id_requisito
        ], Response::HTTP_OK);
    }
}