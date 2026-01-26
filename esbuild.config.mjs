import esbuild from 'esbuild';
import process from 'node:process';
import { sassPlugin } from 'esbuild-sass-plugin';
import postcss from 'postcss';
import autoprefixer from 'autoprefixer';
import postcssPresetEnv from 'postcss-preset-env';

esbuild.build({
    entryPoints: {
        app: './src/main/resources/web/app/app.ts',
        style: './src/main/resources/web/app/style.scss'
    },
    bundle: true,
    outdir: 'src/main/resources/META-INF/resources/static',
    minify: true,
    metafile: true,
    target: ['es2020'],
    plugins: [
        sassPlugin({
            async transform(source, resolveDir) {
                const {css} = await postcss([autoprefixer, postcssPresetEnv({stage: 0})]).process(source, {from: undefined})
                return css
            }
        }),
    ],
}).catch(() => process.exit(1));