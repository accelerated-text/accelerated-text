import { h }            from 'preact';


const MESSAGE =         'The app is in an unexpected state. Please save your work and refresh the page.';

export default ({ className, justIcon, message = MESSAGE }) => {
    const text =        message && message.toString();

    return (
        <a
            className={ className }
            title={ text }
        >
            ❓
            { justIcon ? null : [ ' ', text ] }
        </a>
    );
};
