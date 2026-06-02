import api from './api';

const BASE = '/usuarios';

/**
 * Usuarios activos, para selección como propietario/responsable (F-RP4/F-RP5).
 * Sin try/catch: los errores burbujean a la vista (convención del repo).
 */
export const usuarioService = {
  async listar() {
    const { data } = await api.get(BASE);
    return data;
  },
};

export default usuarioService;
