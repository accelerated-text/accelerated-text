import os                   from 'os';


export default {
    concurrency:            Math.floor( os.cpus().length / 2 ) || 1,
    require:                [ 'esm' ],
    verbose:                true,
};
