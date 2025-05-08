<?php

namespace App\Http\Controllers;

use App\Models\TipoTramite;
use Illuminate\Http\Request;
use Illuminate\Http\Response;

class TipoTramiteController extends Controller
{
    public function index()
    {
        $tipoTramites = TipoTramite::all();
        return response()->json($tipoTramites, Response::HTTP_OK);
    }

    public function store(Request $request)
    {
        $tipoTramite = TipoTramite::create($request->all());
        return response()->json([
            'message' => 'Tipo de trámite creado exitosamente',
            'tipo_tramite' => $tipoTramite
        ], Response::HTTP_CREATED);
    }

    public function show(TipoTramite $tipoTramite)
    {
        return response()->json($tipoTramite, Response::HTTP_OK);
    }

    public function update(Request $request, TipoTramite $tipoTramite)
    {
        $tipoTramite->update($request->all());
        return response()->json([
            'message' => 'Tipo de trámite actualizado exitosamente',
            'tipo_tramite' => $tipoTramite
        ], Response::HTTP_OK);
    }

    public function destroy(TipoTramite $tipoTramite)
    {
        $tipoTramite->delete();
        return response()->json([
            'message' => 'Tipo de trámite eliminado exitosamente',
            'tipo_tramite' => $tipoTramite->id_tipo_tramite
        ], Response::HTTP_OK);
    }
}