let webpack = require("webpack");
let path = require('path');

// JavaScript ファイルに REST クライアントのリクエスト先を書き込みます。
let serviceHost = process.env.SERVICE_HOST || "http://localhost:8080";

module.exports = {
    entry: {
        vendor: [
            "js-joda",
            "jquery"
        ],
        main: ["./src/js/main.ts"],
    },
    output: {
        path: path.resolve(__dirname, 'dist'),
        filename: 'js/[name].js',
    },
    plugins: [
        new webpack.optimize.CommonsChunkPlugin({ name: "vendor" /*, filename: "vendor.bundle.js"*/ }),
        new webpack.optimize.CommonsChunkPlugin({ name: "common", chunks: ["main"] }),
        new webpack.DefinePlugin({
            SERVICE_HOST: JSON.stringify(serviceHost)
        })
    ],
    devtool: "source-map",
    resolve: {
        extensions: [".ts", ".tsx", ".js", ".json"]
    },
    module: {
        rules: [
            // All files with a '.ts' or '.tsx' extension will be handled by 'awesome-typescript-loader'.
            {
                test: /\.tsx?$/,
                loader: "awesome-typescript-loader",
            },

            // All output '.js' files will have any sourcemaps re-processed by 'source-map-loader'.
            {
                enforce: "pre",
                test: /\.js$/,
                loader: "source-map-loader"
            },

            {
                test: /\.scss$/,
                loaders: ["style-loader", "css-loader", "sass-loader"]
            }
        ],
        loaders: [
        ]
    },
    stats: { errorDetails: true }
};