const { createProxyMiddleware } = require('http-proxy-middleware');

module.exports = function (app) {
  console.log('gg');
  app.use(
    '/api',
    createProxyMiddleware({
      target: 'http://localhost:8787',
      changeOrigin: true,
    }),
  );
};
