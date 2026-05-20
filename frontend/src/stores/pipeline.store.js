import { defineStore } from 'pinia';
import { pipelineService } from '@/services/pipeline.service';
import { extractPagination } from '@/utils/pagination';

export const usePipelineStore = defineStore('pipeline', {
  state: () => ({
    pipelines: [],
    pipelineSeleccionado: null,
    pagination: {
      page: 1,
      pageSize: 20,
      totalItems: 0,
      totalPages: 0,
    },
    etapas: [],
    loading: false,
    loadingEtapas: false,
    saving: false,
    error: null,
    filtros: {
      q: '',
      estado: '',
    },
  }),

  getters: {
    pipelinesActivos: (state) => state.pipelines.filter((p) => p.estado === 'ACTIVO'),
    etapasOrdenadas: (state) => [...state.etapas].sort((a, b) => a.orden - b.orden),
    tienePipelineSeleccionado: (state) => state.pipelineSeleccionado !== null,
  },

  actions: {
    // ==================== PIPELINES ====================
    async cargarPipelines() {
      this.loading = true;
      this.error = null;
      try {
        const response = await pipelineService.listar({
          q: this.filtros.q || undefined,
          estado: this.filtros.estado || undefined,
          page: this.pagination.page,
          page_size: this.pagination.pageSize,
        });
        this.pipelines = response.data || [];
        this.pagination = extractPagination(response);
      } catch (err) {
        this.error = err.response?.data?.message || 'Error al cargar pipelines';
      } finally {
        this.loading = false;
      }
    },

    async seleccionarPipeline(id) {
      this.loading = true;
      this.error = null;
      try {
        this.pipelineSeleccionado = await pipelineService.obtenerPorId(id);
        this.etapas = this.pipelineSeleccionado.etapas || [];
      } catch (err) {
        this.error = err.response?.data?.message || 'Error al obtener pipeline';
      } finally {
        this.loading = false;
      }
    },

    async crearPipeline(payload) {
      this.saving = true;
      this.error = null;
      try {
        const nuevo = await pipelineService.crear(payload);
        await this.cargarPipelines();
        return nuevo;
      } catch (err) {
        this.error = err.response?.data?.message || 'Error al crear pipeline';
        throw err;
      } finally {
        this.saving = false;
      }
    },

    async actualizarPipeline(id, payload) {
      this.saving = true;
      this.error = null;
      try {
        const actualizado = await pipelineService.actualizar(id, payload);
        await this.cargarPipelines();
        if (this.pipelineSeleccionado?.id === id) {
          this.pipelineSeleccionado = actualizado;
        }
        return actualizado;
      } catch (err) {
        this.error = err.response?.data?.message || 'Error al actualizar pipeline';
        throw err;
      } finally {
        this.saving = false;
      }
    },

    // ==================== ETAPAS ====================
    async cargarEtapas(pipelineId) {
      this.loadingEtapas = true;
      try {
        this.etapas = await pipelineService.listarEtapas(pipelineId);
      } catch (err) {
        this.error = err.response?.data?.message || 'Error al cargar etapas';
      } finally {
        this.loadingEtapas = false;
      }
    },

    async crearEtapa(pipelineId, payload) {
      this.saving = true;
      this.error = null;
      try {
        const nueva = await pipelineService.crearEtapa(pipelineId, payload);
        await this.cargarEtapas(pipelineId);
        return nueva;
      } catch (err) {
        this.error = err.response?.data?.message || 'Error al crear etapa';
        throw err;
      } finally {
        this.saving = false;
      }
    },

    async actualizarEtapa(pipelineId, etapaId, payload) {
      this.saving = true;
      this.error = null;
      try {
        const actualizada = await pipelineService.actualizarEtapa(pipelineId, etapaId, payload);
        await this.cargarEtapas(pipelineId);
        return actualizada;
      } catch (err) {
        this.error = err.response?.data?.message || 'Error al actualizar etapa';
        throw err;
      } finally {
        this.saving = false;
      }
    },

    // ==================== FILTROS ====================
    setFiltros(filtros) {
      this.filtros = { ...this.filtros, ...filtros };
      this.pagination.page = 1;
      this.cargarPipelines();
    },

    setPagina(page) {
      this.pagination.page = page;
      this.cargarPipelines();
    },

    limpiarSeleccion() {
      this.pipelineSeleccionado = null;
      this.etapas = [];
    },

    limpiarError() {
      this.error = null;
    },
  },
});
