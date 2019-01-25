import { h }            from 'preact';


const MESSAGE =         'The app is in an unexpected state. Please save your work and refresh the page.';

export default ({ className, justIcon }) =>
    <a className={ className } title={ MESSAGE }>
        { justIcon ? '❓' : MESSAGE }
    </a>;
