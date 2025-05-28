<?php

namespace App\Http\Controllers;

use App\Models\TipoTramite;
use Illuminate\Http\Request;
use Illuminate\Http\Response;
use Illuminate\Support\Facades\Validator;

class TipoTramiteController extends Controller
{
    
    private $rules = [
        'nombre_tipo' => 'required|string|max:255',
    ];

    private $traductionAttributes = [
        'nombre_tipo' => 'nombre del tipo de trámite',
    ];

    
    public function applyValidator(Request $request)
    {
        $validator = Validator::make($request->all(), $this->rules);
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
        $tipoTramites = TipoTramite::all();
        
        return response()->json($tipoTramites, Response::HTTP_OK);
    }

    public function store(Request $request)
    {
        $data = $this->applyValidator($request);
        if (!empty($data)) {
            return $data;
        }
        $tipoTramite = TipoTramite::create($request->all());
        return response()->json([
            'message' => 'Tipo de trámite creado exitosamente',
            'tipo_tramite' => $tipoTramite
        ], Response::HTTP_CREATED);
    }

    public function show(TipoTramite $tipoTramite)
    {
        $tipoTramite->load('tramites');
        return response()->json($tipoTramite, Response::HTTP_OK);
    }

    public function update(Request $request, TipoTramite $tipoTramite)
    {
        $data = $this->applyValidator($request);
        if (!empty($data)) {
            return $data;
        }
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