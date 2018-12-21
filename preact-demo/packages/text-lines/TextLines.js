import { h }            from 'preact';


export default ({ text }) =>
    <div>
        { text.split( '\n' ).map( line =>
            <div>{ line }</div>
        )}
    </div>;
