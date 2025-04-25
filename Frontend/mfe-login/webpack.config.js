const ModuleFederationPlugin = require("webpack/lib/container/ModuleFederationPlugin");

module.exports = {
  output: {
    uniqueName: "mfe-login",
    publicPath: "auto",
    clean: true, 
  },
  optimization: {
    runtimeChunk: false,
  },
  plugins: [
    new ModuleFederationPlugin({
      name: "mfe_login",
      filename: "remoteEntry.js",
      exposes: {
        './LoginModule': './src/app/login/login.module.ts',
      },
      shared: {
        "@angular/core": { singleton: true, strictVersion: true, requiredVersion: 'auto' },
        "@angular/common": { singleton: true, strictVersion: true, requiredVersion: 'auto' },
        "@angular/router": { singleton: true, strictVersion: true, requiredVersion: 'auto' },
      },
    }),
  ],
};
