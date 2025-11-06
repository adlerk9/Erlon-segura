require('dotenv').config();
const express = require('express');
const app = express();
const routes = require('./authRoutes');

app.use(express.json());
app.use('/api/v1/auth', routes);

const port = process.env.PORT || 3000;
app.listen(port, () => console.log(`🚀 Auth Service rodando na porta ${port}`));