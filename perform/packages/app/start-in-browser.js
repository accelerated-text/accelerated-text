const { h, render } =       require( 'preact' );

const App =                 require( './App' ).default;
require( './global-styles.sass' );

if( module.hot ) {
    require( 'preact/debug' );
}

render( <App />, document.body );
