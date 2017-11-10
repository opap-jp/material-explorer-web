// この変数（プレースホルダ）は webpack によって置換されます。
declare let SERVICE_HOST: string;

export class MaterialExplorer {
    static host: string = SERVICE_HOST;
    static request: (path :string) => Promise<Response> = (path) => fetch(MaterialExplorer.host + path);

    public static readonly resources = (function() {
        let request = MaterialExplorer.request;

        return  {
            person: () => request("/person")
                .then(r => r.json()),
            repositories: () => request("/repositories")
                .then(r => r.json()) as Promise<{ items: Repository[] }>,
        };
    })();
}

export interface Repository {
    id: string;
    name: string;
    lastActivityAt: string;
}
