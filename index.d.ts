declare module 'rn-detect-soft-nav' {
    export function addListeners({ onShown, onHidden }: { onShown: () => any, onHidden: () => any });

    export function isVisible(): Promise<boolean>;

    export function removeListener(): any;

    export const hasSoftKeys: boolean;

    export const hasSoftKeysHeight: number;
}
