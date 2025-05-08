<?php

namespace App\Http\Controllers;

use App\Models\Tramite;
use Illuminate\Http\Request;
use Illuminate\Http\Response;

class TramiteController extends Controller
{
    public function index()
    {
        $tramites = Tramite::with(['usuario', 'tipoTramite'])->get();
        return response()->json($tramites, Response::HTTP_OK);
    }

    public function store(Request $request)
    {
        $tramite = Tramite::create($request->all());
        return response()->json([
            'message' => 'Trámite creado exitosamente',
            'tramite' => $tramite
        ], Response::HTTP_CREATED);
    }

    public function show(Tramite $tramite)
    {
        $tramite->load(['usuario', 'tipoTramite']);
        return response()->json($tramite, Response::HTTP_OK);
    }

    public function update(Request $request, Tramite $tramite)
    {
        $tramite->update($request->all());
        return response()->json([
            'message' => 'Trámite actualizado exitosamente',
            'tramite' => $tramite
        ], Response::HTTP_OK);
    }

    public function destroy(Tramite $tramite)
    {
        $tramite->delete();
        return response()->json([
            'message' => 'Trámite eliminado exitosamente',
            'tramite' => $tramite->id_tramite
        ], Response::HTTP_OK);
    }
}