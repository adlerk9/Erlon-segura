const pool = require('./db');

module.exports = {
  signup: async (req, res) => {
    res.send('RF01 - Criar nova conta');
  },
  login: async (req, res) => {
    res.send('RF02 - Fazer login');
  },
  recuperarSenha: async (req, res) => {
    res.send('RF03 - Recuperar senha');
  },
  logout: async (req, res) => {
    res.send('RF04 - Fazer logout');
  },
  me: async (req, res) => {
    res.send('RF05 - Retornar dados do usuário');
  }
};