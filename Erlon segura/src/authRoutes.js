const express = require('express');
const router = express.Router();
const auth = require('./authController');

router.post('/signup', auth.signup);
router.post('/login', auth.login);
router.post('/recuperar-senha', auth.recuperarSenha);
router.post('/logout', auth.logout);
router.get('/me', auth.me);

module.exports = router;