import { defineStore } from 'pinia';
import { empresaService } from '@/services/empresa.service';

export const useEmpresaStore = defineStore('empresa', {
  state: () => ({
    empresas: [],
    empresaSeleccionada: null,
    pagination: { page: 1, pageSize: 20, totalItems: 0, totalPages: 0 },
    loading: false,
    saving: false,
    error: null,
    filtros: { q: '', estado: '' },

    // Catálogos
    tiposDocumento: [],
    paises: [],
    departamentos: [],
    municipios: [],
    catalogosCargados: false,
    loadingCatalogos: false,
  }),

  getters: {
    empresasActivas: (state) => state.empresas.filter((e) => e.estado === 'ACTIVA'),
  },

  actions: {
    // ==================== CATÁLOGOS ====================
    async cargarCatalogos() {
      if (this.catalogosCargados) return;
      this.loadingCatalogos = true;
      try {
        const [tiposDoc, paises, departamentos] = await Promise.all([
          empresaService.listarTiposDocumento(),
          empresaService.listarPaises(),
          empresaService.listarDepartamentos(),
        ]);
        this.tiposDocumento = tiposDoc;
        this.paises = paises;
        this.departamentos = departamentos;
        this.catalogosCargados = true;
      } catch (err) {
        console.error('Error cargando catálogos:', err);
      } finally {
        this.loadingCatalogos = false;
      }
    },

    async cargarMunicipios(codigoDepartamento) {
      try {
        this.municipios = await empresaService.listarMunicipios(codigoDepartamento);
      } catch (err) {
        console.error('Error cargando municipios:', err);
        this.municipios = [];
      }
    },

    // ==================== EMPRESAS ====================
    async cargarEmpresas() {
      this.loading = true;
      this.error = null;
      try {
        const response = await empresaService.listar({
          q: this.filtros.q || undefined,
          estado: this.filtros.estado || undefined,
          page: this.pagination.page,
          page_size: this.pagination.pageSize,
        });
        this.empresas = response.data || [];
        this.pagination = {
          page: response.page || 1,
          pageSize: response.pageSize || 20,
          totalItems: response.totalItems || 0,
          totalPages: response.totalPages || 0,
        };
      } catch (err) {
        this.error = err.response?.data?.message || 'Error al cargar empresas';
      } finally {
        this.loading = false;
      }
    },

    async obtenerEmpresa(id) {
      this.loading = true;
      this.error = null;
      try {
        this.empresaSeleccionada = await empresaService.obtenerPorId(id);
      } catch (err) {
        this.error = err.response?.data?.message || 'Error al obtener empresa';
      } finally {
        this.loading = false;
      }
    },

    async crearEmpresa(payload) {
      this.saving = true;
      this.error = null;
      try {
        const nueva = await empresaService.crear(payload);
        await this.cargarEmpresas();
        return nueva;
      } catch (err) {
        this.error = err.response?.data?.message || 'Error al crear empresa';
        throw err;
      } finally {
        this.saving = false;
      }
    },

    async actualizarEmpresa(id, payload) {
      this.saving = true;
      this.error = null;
      try {
        const actualizada = await empresaService.actualizar(id, payload);
        await this.cargarEmpresas();
        if (this.empresaSeleccionada?.id === id) this.empresaSeleccionada = actualizada;
        return actualizada;
      } catch (err) {
        this.error = err.response?.data?.message || 'Error al actualizar empresa';
        throw err;
      } finally {
        this.saving = false;
      }
    },

    setFiltros(filtros) {
      this.filtros = { ...this.filtros, ...filtros };
      this.pagination.page = 1;
      this.cargarEmpresas();
    },

    setPagina(page) {
      this.pagination.page = page;
      this.cargarEmpresas();
    },

    limpiarSeleccion() { this.empresaSeleccionada = null; },
    limpiarError() { this.error = null; },
  },
});
