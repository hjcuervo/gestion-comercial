import { defineStore } from 'pinia';
import { personaService } from '@/services/persona.service';
import { empresaService } from '@/services/empresa.service';
import { extractPagination } from '@/utils/pagination';

export const usePersonaStore = defineStore('persona', {
  state: () => ({
    personas: [],
    personaSeleccionada: null,
    pagination: { page: 1, pageSize: 20, totalItems: 0, totalPages: 0 },
    loading: false,
    saving: false,
    error: null,
    filtros: { q: '', empresa_id: null },
    empresasDisponibles: [],
    loadingEmpresas: false,
  }),

  getters: {
    personasActivas: (state) => state.personas.filter((p) => p.activo),
  },

  actions: {
    async cargarPersonas() {
      this.loading = true;
      this.error = null;
      try {
        const params = {
          q: this.filtros.q || undefined,
          empresa_id: this.filtros.empresa_id || undefined,
          page: this.pagination.page,
          page_size: this.pagination.pageSize,
        };
        const response = await personaService.listar(params);
        this.personas = response.data || [];
        this.pagination = extractPagination(response);
      } catch (err) {
        this.error = err.response?.data?.message || 'Error al cargar contactos';
      } finally {
        this.loading = false;
      }
    },

    async seleccionarPersona(id) {
      this.loading = true;
      this.error = null;
      try {
        this.personaSeleccionada = await personaService.obtenerPorId(id);
      } catch (err) {
        this.error = err.response?.data?.message || 'Error al obtener contacto';
      } finally {
        this.loading = false;
      }
    },

    async crearPersona(payload) {
      this.saving = true;
      this.error = null;
      try {
        const nueva = await personaService.crear(payload);
        await this.cargarPersonas();
        return nueva;
      } catch (err) {
        this.error = err.response?.data?.message || 'Error al crear contacto';
        throw err;
      } finally {
        this.saving = false;
      }
    },

    async actualizarPersona(id, payload) {
      this.saving = true;
      this.error = null;
      try {
        const actualizada = await personaService.actualizar(id, payload);
        await this.cargarPersonas();
        if (this.personaSeleccionada?.id === id) this.personaSeleccionada = actualizada;
        return actualizada;
      } catch (err) {
        this.error = err.response?.data?.message || 'Error al actualizar contacto';
        throw err;
      } finally {
        this.saving = false;
      }
    },

    async asociarEmpresa(personaId, payload) {
      this.saving = true;
      this.error = null;
      try {
        const result = await personaService.asociarEmpresa(personaId, payload);
        await this.seleccionarPersona(personaId);
        await this.cargarPersonas();
        return result;
      } catch (err) {
        this.error = err.response?.data?.message || 'Error al asociar empresa';
        throw err;
      } finally {
        this.saving = false;
      }
    },

    async desasociarEmpresa(personaId, empresaId) {
      this.saving = true;
      this.error = null;
      try {
        await personaService.desasociarEmpresa(personaId, empresaId);
        await this.seleccionarPersona(personaId);
        await this.cargarPersonas();
      } catch (err) {
        this.error = err.response?.data?.message || 'Error al desasociar empresa';
        throw err;
      } finally {
        this.saving = false;
      }
    },

    async cargarEmpresasDisponibles() {
      this.loadingEmpresas = true;
      try {
        const response = await empresaService.listar({ estado: 'ACTIVA', page_size: 100 });
        this.empresasDisponibles = response.data || [];
      } catch (err) {
        this.empresasDisponibles = [];
      } finally {
        this.loadingEmpresas = false;
      }
    },

    setFiltros(filtros) {
      this.filtros = { ...this.filtros, ...filtros };
      this.pagination.page = 1;
      this.cargarPersonas();
    },

    setPagina(page) { this.pagination.page = page; this.cargarPersonas(); },
    limpiarSeleccion() { this.personaSeleccionada = null; },
    limpiarError() { this.error = null; },
  },
});
