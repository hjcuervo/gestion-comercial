import { defineStore } from 'pinia';
import { oportunidadService } from '@/services/oportunidad.service';
import { pipelineService } from '@/services/pipeline.service';
import { empresaService } from '@/services/empresa.service';

export const useOportunidadStore = defineStore('oportunidad', {
  state: () => ({
    oportunidades: [],
    oportunidadSeleccionada: null,
    loading: false,
    saving: false,
    error: null,

    // Kanban
    pipelineActivo: null,
    pipelinesActivos: [],
    etapasDelPipeline: [],

    // Para modales
    empresasActivas: [],
    loadingRefs: false,
  }),

  getters: {
    // Agrupa oportunidades por etapaId para el kanban
    oportunidadesPorEtapa: (state) => {
      const map = {};
      for (const etapa of state.etapasDelPipeline) {
        map[etapa.id] = [];
      }
      for (const op of state.oportunidades) {
        if (map[op.etapaId]) {
          map[op.etapaId].push(op);
        }
      }
      return map;
    },

    totalValorPipeline: (state) => {
      return state.oportunidades
        .filter(o => o.estadoMacro === 'ABIERTA' || o.estadoMacro === 'SEGUIMIENTO')
        .reduce((sum, o) => sum + (o.valorEstimado || 0), 0);
    },

    countAbiertas: (state) => state.oportunidades.filter(o => o.estadoMacro === 'ABIERTA' || o.estadoMacro === 'SEGUIMIENTO').length,
  },

  actions: {
    async cargarPipelinesActivos() {
      try {
        this.pipelinesActivos = await pipelineService.listarActivos();
        if (this.pipelinesActivos.length && !this.pipelineActivo) {
          await this.seleccionarPipeline(this.pipelinesActivos[0].id);
        }
      } catch (err) {
        this.error = err.response?.data?.message || 'Error al cargar pipelines';
      }
    },

    async seleccionarPipeline(pipelineId) {
      this.loading = true;
      this.error = null;
      try {
        const pipeline = await pipelineService.obtenerPorId(pipelineId);
        this.pipelineActivo = pipeline;
        // RB-EI-1: el Kanban solo muestra etapas activas. Las inactivas existen
        // en BD pero quedan ocultas del flujo operativo (se administran en Configuración).
        this.etapasDelPipeline = (pipeline.etapas || [])
          .filter(e => e.estado === 'ACTIVA')
          .sort((a, b) => a.orden - b.orden);
        await this.cargarOportunidades();
      } catch (err) {
        this.error = err.response?.data?.message || 'Error al cargar pipeline';
      } finally {
        this.loading = false;
      }
    },

    async cargarOportunidades() {
      if (!this.pipelineActivo) return;
      this.loading = true;
      try {
        const response = await oportunidadService.listar({
          pipeline_id: this.pipelineActivo.id,
          page_size: 100,
        });
        this.oportunidades = response.data || [];
      } catch (err) {
        this.error = err.response?.data?.message || 'Error al cargar oportunidades';
      } finally {
        this.loading = false;
      }
    },

    async crearOportunidad(payload) {
      this.saving = true;
      this.error = null;
      try {
        const nueva = await oportunidadService.crear(payload);
        await this.cargarOportunidades();
        return nueva;
      } catch (err) {
        this.error = err.response?.data?.message || 'Error al crear oportunidad';
        throw err;
      } finally {
        this.saving = false;
      }
    },

    async actualizarOportunidad(id, payload) {
      this.saving = true;
      this.error = null;
      try {
        const actualizada = await oportunidadService.actualizar(id, payload);
        await this.cargarOportunidades();
        return actualizada;
      } catch (err) {
        this.error = err.response?.data?.message || 'Error al actualizar oportunidad';
        throw err;
      } finally {
        this.saving = false;
      }
    },

    async moverEtapa(oportunidadId, etapaId) {
      this.saving = true;
      this.error = null;
      try {
        await oportunidadService.moverEtapa(oportunidadId, { etapaId });
        await this.cargarOportunidades();
      } catch (err) {
        this.error = err.response?.data?.message || 'Error al mover oportunidad';
        throw err;
      } finally {
        this.saving = false;
      }
    },

    async cerrarOportunidad(id, payload) {
      this.saving = true;
      this.error = null;
      try {
        await oportunidadService.cerrar(id, payload);
        await this.cargarOportunidades();
      } catch (err) {
        this.error = err.response?.data?.message || 'Error al cerrar oportunidad';
        throw err;
      } finally {
        this.saving = false;
      }
    },

    async cargarEmpresasActivas() {
      this.loadingRefs = true;
      try {
        const response = await empresaService.listar({ estado: 'ACTIVA', page_size: 100 });
        this.empresasActivas = response.data || [];
      } catch (err) {
        this.empresasActivas = [];
      } finally {
        this.loadingRefs = false;
      }
    },

    limpiarError() { this.error = null; },
  },
});
