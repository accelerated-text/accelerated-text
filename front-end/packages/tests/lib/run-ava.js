import 'dotenv-extended/config';

import { resolve }          from 'path';
import { spawn }            from 'child_process';

import devServer            from '../../webpack/test-server';


const cwd =                 resolve( __dirname, '../../..' );
const avaBin =              resolve( cwd, 'node_modules/.bin/ava' );

devServer
    .then( server => {

        const argv =        [ avaBin, ...process.argv.slice( 2 ) ];
        const avaProcess =  spawn( 'node', argv, {
            cwd,
            stdio:          'inherit',
        });

        avaProcess.on( 'close', signal => {
            server.close();
            process.exit( signal );
        });
    });
